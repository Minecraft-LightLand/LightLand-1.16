package com.lcy0x1.core.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Capable of serializing primitive type, Arrays, Item, ItemStacl, Ingredient
 * <br>
 * Not capable of handling inheritance, collections
 */
public class Serializer {

    public static final Map<Class<?>, ClassHandler<?>> MAP = new HashMap<>();

    static {
        new ClassHandler<>(long.class, JsonElement::getAsLong, PacketBuffer::readLong, PacketBuffer::writeLong);
        new ClassHandler<>(int.class, JsonElement::getAsInt, PacketBuffer::readInt, PacketBuffer::writeInt);
        new ClassHandler<Short>(short.class, JsonElement::getAsShort, PacketBuffer::readShort, PacketBuffer::writeShort);
        new ClassHandler<Byte>(byte.class, JsonElement::getAsByte, PacketBuffer::readByte, PacketBuffer::writeByte);
        new ClassHandler<Character>(char.class, JsonElement::getAsCharacter, PacketBuffer::readChar, PacketBuffer::writeChar);
        new ClassHandler<>(boolean.class, JsonElement::getAsBoolean, PacketBuffer::readBoolean, PacketBuffer::writeBoolean);
        new ClassHandler<>(double.class, JsonElement::getAsDouble, PacketBuffer::readDouble, PacketBuffer::writeDouble);
        new ClassHandler<>(float.class, JsonElement::getAsFloat, PacketBuffer::readFloat, PacketBuffer::writeFloat);
        new ClassHandler<>(String.class, JsonElement::getAsString, PacketBuffer::readUtf, PacketBuffer::writeUtf);
        new ClassHandler<>(ItemStack.class, (e) -> ShapedRecipe.itemFromJson(e.getAsJsonObject()), PacketBuffer::readItem, PacketBuffer::writeItem);
        new ClassHandler<>(Ingredient.class, Ingredient::fromJson, Ingredient::fromNetwork, (p, o) -> o.toNetwork(p));
        new ClassHandler<>(CompoundNBT.class, (e) -> new CompoundNBT(), PacketBuffer::readAnySizeNbt, PacketBuffer::writeNbt);

        new StringClassHandler<>(ResourceLocation.class, ResourceLocation::new, ResourceLocation::toString);

        new RLClassHandler<>(Item.class, () -> ForgeRegistries.ITEMS);
    }

    @SuppressWarnings("unchecked")
    public static <T> T from(JsonElement obj, Class<T> cls, T ans) {
        return ExceptionHandler.get(() -> (T) fromRaw(obj, cls, ans, null));
    }

    @SuppressWarnings("unchecked")
    public static <T> T from(PacketBuffer buf, Class<T> cls, T ans) {
        return ExceptionHandler.get(() -> (T) fromRaw(buf, cls, ans, null));
    }

    public static Object fromImpl(JsonObject obj, Class<?> cls, Object ans, SerialClass.SerialField anno) throws Exception {
        if (cls == Object.class) {
            cls = Class.forName(obj.get("_class").getAsString());
        }
        if (cls.getAnnotation(SerialClass.class) == null)
            throw new Exception("invalid class " + cls + " with object " + obj.toString());
        if (ans == null)
            ans = cls.newInstance();
        while (cls.getAnnotation(SerialClass.class) != null) {
            for (Field f : cls.getDeclaredFields()) {
                if (f.getAnnotation(SerialClass.SerialField.class) != null) {
                    if (obj.has(f.getName()))
                        f.set(ans, fromRaw(obj.get(f.getName()), f.getType(), null, f.getAnnotation(SerialClass.SerialField.class)));
                }
            }
            cls = cls.getSuperclass();
        }
        cls = ans.getClass();
        for (Method method : cls.getMethods())
            if (method.isAnnotationPresent(SerialClass.OnInject.class))
                method.invoke(ans);
        return ans;
    }

    public static Object fromImpl(PacketBuffer buf, Class<?> cls, Object ans, SerialClass.SerialField anno) throws Exception {
        if (cls == Object.class) {
            cls = Class.forName(buf.readUtf());
        }
        if (cls.getAnnotation(SerialClass.class) == null)
            throw new Exception("cannot deserialize " + cls);
        if (ans == null)
            ans = cls.newInstance();
        while (cls.getAnnotation(SerialClass.class) != null) {
            TreeMap<String, Field> map = new TreeMap<>();
            for (Field f : cls.getDeclaredFields()) {
                if (f.getAnnotation(SerialClass.SerialField.class) != null) {
                    map.put(f.getName(), f);
                }
            }
            for (Field f : map.values()) {
                f.set(ans, fromRaw(buf, f.getType(), null, f.getAnnotation(SerialClass.SerialField.class)));
            }
            cls = cls.getSuperclass();
        }
        for (Method method : cls.getMethods())
            if (method.isAnnotationPresent(SerialClass.OnInject.class))
                method.invoke(ans);
        return ans;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object fromRaw(JsonElement e, Class<?> cls, Object ans, SerialClass.SerialField anno) throws Exception {
        if (cls.isArray()) {
            JsonArray arr = e.getAsJsonArray();
            Class<?> com = cls.getComponentType();
            int n = arr.size();
            if (ans == null) ans = Array.newInstance(com, n);
            for (int i = 0; i < n; i++) {
                Array.set(ans, i, fromRaw(arr.get(i), com, null, anno));
            }
            return ans;
        }
        if (Map.class.isAssignableFrom(cls)) {
            if (ans == null)
                ans = cls.newInstance();
            Class<?> ckey = anno.generic()[0];
            Class<?> cval = anno.generic()[1];
            if (e.isJsonArray()) {
                for (JsonElement je : e.getAsJsonArray()) {
                    JsonObject jeo = je.getAsJsonObject();
                    ((Map) ans).put(fromRaw(jeo.get("_key"), ckey, null, null), fromRaw(jeo.get("_val"), cval, null, null));
                }
                return ans;
            }
            if (e.isJsonObject() && ckey == String.class) {
                for (Map.Entry<String, JsonElement> ent : e.getAsJsonObject().entrySet()) {
                    ((Map) ans).put(ent.getKey(), fromRaw(ent.getValue(), cval, null, null));
                }
                return ans;
            }
        }
        if (cls.isEnum())
            return Enum.valueOf((Class) cls, e.getAsString());
        if (MAP.containsKey(cls))
            return MAP.get(cls).fromJson.apply(e);
        return fromImpl(e.getAsJsonObject(), cls, ans, anno);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object fromRaw(PacketBuffer buf, Class<?> cls, Object ans, SerialClass.SerialField anno) throws Exception {
        if (cls.isArray()) {
            int n = buf.readInt();
            Class<?> com = cls.getComponentType();
            if (ans == null)
                ans = Array.newInstance(com, n);
            for (int i = 0; i < n; i++) {
                Array.set(ans, i, fromRaw(buf, com, null, anno));
            }
            return ans;
        }
        if (Map.class.isAssignableFrom(cls)) {
            if (ans == null)
                ans = cls.newInstance();
            int n = buf.readInt();
            Class<?> ckey = anno.generic()[0];
            Class<?> cval = anno.generic()[1];
            for (int i = 0; i < n; i++) {
                Object key = fromRaw(buf, ckey, null, null);
                Object val = fromRaw(buf, cval, null, null);
                ((Map) ans).put(key, val);
            }
            return ans;
        }
        if (cls.isEnum())
            return Enum.valueOf((Class) cls, buf.readUtf());
        if (MAP.containsKey(cls))
            return MAP.get(cls).fromPacket.apply(buf);
        return fromImpl(buf, cls, ans, anno);
    }

    public static <T> void to(PacketBuffer buf, T obj) {
        ExceptionHandler.run(() -> toImpl(buf, obj.getClass(), obj, null));
    }

    public static void toImpl(PacketBuffer buf, Class<?> cls, Object obj, SerialClass.SerialField anno) throws Exception {
        if (cls == Object.class) {
            cls = obj.getClass();
            buf.writeUtf(cls.getName());
        }
        if (cls.getAnnotation(SerialClass.class) == null)
            throw new Exception("cannot serialize " + cls);
        TreeMap<String, Field> map = new TreeMap<>();
        for (Field f : cls.getDeclaredFields()) {
            if (f.getAnnotation(SerialClass.SerialField.class) != null) {
                map.put(f.getName(), f);
            }
        }
        for (Field f : map.values()) {
            toRaw(buf, f.getType(), f.get(obj), f.getAnnotation(SerialClass.SerialField.class));
        }
    }

    public static void toRaw(PacketBuffer buf, Class<?> cls, Object obj, SerialClass.SerialField anno) throws Exception {
        if (cls.isArray()) {
            int n = Array.getLength(obj);
            buf.writeInt(n);
            Class<?> com = cls.getComponentType();
            for (int i = 0; i < n; i++) {
                toRaw(buf, com, Array.get(obj, i), anno);
            }
        } else if (Map.class.isAssignableFrom(cls)) {
            Map<?, ?> map = (Map<?, ?>) obj;
            buf.writeInt(map.size());
            Class<?> ckey = anno.generic()[0];
            Class<?> cval = anno.generic()[1];
            for (Map.Entry<?, ?> ent : map.entrySet()) {
                toRaw(buf, ckey, ent.getKey(), null);
                toRaw(buf, cval, ent.getValue(), null);
            }
        } else if (cls.isEnum())
            buf.writeUtf(((Enum<?>) obj).name());
        else if (MAP.containsKey(cls))
            MAP.get(cls).toPacket.accept(buf, obj);
        else
            toImpl(buf, cls, obj, anno);

    }

    public static class ClassHandler<T> {

        public Function<JsonElement, ?> fromJson;
        public Function<PacketBuffer, ?> fromPacket;
        public BiConsumer<PacketBuffer, Object> toPacket;

        @SuppressWarnings("unchecked")
        public ClassHandler(Class<?> cls, Function<JsonElement, T> fj, Function<PacketBuffer, T> fp,
                            BiConsumer<PacketBuffer, T> tp) {
            this.fromJson = fj;
            this.fromPacket = fp;
            this.toPacket = (BiConsumer<PacketBuffer, Object>) tp;
            MAP.put(cls, this);
        }

    }

    public static class StringClassHandler<T> extends ClassHandler<T> {

        public StringClassHandler(Class<?> cls, Function<String, T> fj, Function<T, String> tp) {
            super(cls, e -> {
                if (e.isJsonNull())
                    return null;
                String str = e.getAsString();
                if (str.length() == 0)
                    return null;
                return fj.apply(str);
            }, p -> {
                String str = p.readUtf();
                if (str.length() == 0)
                    return null;
                return fj.apply(str);
            }, (p, t) -> p.writeUtf(t == null ? "" : tp.apply(t)));
        }

    }

    public static class RLClassHandler<T extends IForgeRegistryEntry<T>> extends ClassHandler<T> {

        public RLClassHandler(Class<?> cls, Supplier<IForgeRegistry<T>> r) {
            super(cls, e -> e.isJsonNull() ? null : r.get().getValue(new ResourceLocation(e.getAsString())),
                    p -> {
                        String str = p.readUtf();
                        if (str.length() == 0)
                            return null;
                        return r.get().getValue(new ResourceLocation(p.readUtf()));
                    },
                    (p, t) -> p.writeUtf(t == null ? "" : t.getRegistryName().toString()));
        }

    }

}