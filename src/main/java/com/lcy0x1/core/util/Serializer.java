package com.lcy0x1.core.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Capable of serializing primitive type, Arrays, Item, ItemStacl, Ingredient
 * <br>
 * Not capable of handling inheritance, collections
 */
public class Serializer {

    private static class ClassHandler<T> {

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

    public static final Map<Class<?>, ClassHandler<?>> MAP = new HashMap<>();

    static {
        new ClassHandler<Long>(long.class, e -> e.getAsLong(), p -> p.readLong(), (p, o) -> p.writeLong(o));
        new ClassHandler<Integer>(int.class, e -> e.getAsInt(), p -> p.readInt(), (p, o) -> p.writeInt(o));
        new ClassHandler<Short>(short.class, e -> e.getAsShort(), p -> p.readShort(),
                (p, o) -> p.writeShort(o));
        new ClassHandler<Byte>(byte.class, e -> e.getAsByte(), p -> p.readByte(), (p, o) -> p.writeByte(o));
        new ClassHandler<Character>(char.class, e -> e.getAsCharacter(), p -> p.readChar(),
                (p, o) -> p.writeChar(o));
        new ClassHandler<Boolean>(boolean.class, e -> e.getAsBoolean(), p -> p.readBoolean(),
                (p, o) -> p.writeBoolean(o));
        new ClassHandler<String>(String.class, e -> e.getAsString(), p -> p.readUtf(),
                (p, o) -> p.writeUtf(o));

        new ClassHandler<Item>(Item.class, e -> Registry.ITEM.get(new ResourceLocation(e.getAsString())),
                p -> Item.byId(p.readVarInt()), (p, o) -> p.writeVarInt(Item.getId(o)));
        new ClassHandler<ItemStack>(ItemStack.class, (e) -> ShapedRecipe.itemFromJson(e.getAsJsonObject()),
                p -> p.readItem(), (p, o) -> p.writeItem(o));
        new ClassHandler<Ingredient>(Ingredient.class, e -> Ingredient.fromJson(e),
                p -> Ingredient.fromNetwork(p), (p, o) -> o.toNetwork(p));

    }

    @SuppressWarnings("unchecked")
    public static <T> T from(JsonObject obj, Class<T> cls, T ans) {
        return ExceptionHandler.get(() -> (T) fromImpl(obj, cls, ans));
    }

    @SuppressWarnings("unchecked")
    public static <T> T from(PacketBuffer buf, Class<T> cls, T ans) {
        return ExceptionHandler.get(() -> (T) fromImpl(buf, cls, ans));
    }

    public static Object fromImpl(JsonObject obj, Class<?> cls, Object ans) throws Exception {
        if (cls.getAnnotation(SerialClass.class) == null)
            throw new Exception("invalid class " + cls + " with object " + obj.toString());
        if (ans == null)
            ans = cls.newInstance();
        while (cls.getAnnotation(SerialClass.class) != null) {
            for (Field f : cls.getDeclaredFields()) {
                if (f.getAnnotation(SerialClass.SerialField.class) != null) {
                    f.set(ans, fromRaw(obj.get(f.getName()), f.getType()));
                }
            }
            cls = cls.getSuperclass();
        }
        for (Method method : cls.getMethods())
            if (method.isAnnotationPresent(SerialClass.OnInject.class))
                method.invoke(ans);
        return ans;
    }

    public static Object fromImpl(PacketBuffer buf, Class<?> cls, Object ans) throws Exception {
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
                f.set(ans, fromRaw(buf, f.getType()));
            }
            cls = cls.getSuperclass();
        }
        for (Method method : cls.getMethods())
            if (method.isAnnotationPresent(SerialClass.OnInject.class))
                method.invoke(ans);
        return ans;
    }

    public static Object fromRaw(JsonElement e, Class<?> cls) throws Exception {
        if (cls.isArray()) {
            JsonArray arr = e.getAsJsonArray();
            Class<?> com = cls.getComponentType();
            int n = arr.size();
            Object ans = Array.newInstance(com, n);
            for (int i = 0; i < n; i++) {
                Array.set(ans, i, fromRaw(arr.get(i), com));
            }
            return ans;
        }
        if (cls.isEnum())
            return Enum.valueOf((Class)cls, e.getAsString());
        if (MAP.containsKey(cls))
            return MAP.get(cls).fromJson.apply(e);
        return fromImpl(e.getAsJsonObject(), cls, null);
    }

    public static Object fromRaw(PacketBuffer buf, Class<?> cls) throws Exception {
        if (cls.isArray()) {
            int n = buf.readInt();
            Class<?> com = cls.getComponentType();
            Object ans = Array.newInstance(com, n);
            for (int i = 0; i < n; i++) {
                Array.set(ans, i, fromRaw(buf, com));
            }
            return ans;
        }
        if (cls.isEnum())
            return Enum.valueOf((Class)cls, buf.readUtf());
        if (MAP.containsKey(cls))
            return MAP.get(cls).fromPacket.apply(buf);
        return fromImpl(buf, cls, null);
    }

    public static <T> void to(PacketBuffer buf, T obj) {
        ExceptionHandler.run(() -> toImpl(buf, obj.getClass(), obj));
    }

    public static void toImpl(PacketBuffer buf, Class<?> cls, Object obj) throws Exception {
        if (cls.getAnnotation(SerialClass.class) == null)
            throw new Exception("cannot serialize " + cls);
        TreeMap<String, Field> map = new TreeMap<>();
        for (Field f : cls.getDeclaredFields()) {
            if (f.getAnnotation(SerialClass.SerialField.class) != null) {
                map.put(f.getName(), f);
            }
        }
        for (Field f : map.values()) {
            toRaw(buf, f.getType(), f.get(obj));
        }
    }

    public static void toRaw(PacketBuffer buf, Class<?> cls, Object obj) throws Exception {
        if (cls.isArray()) {
            int n = Array.getLength(obj);
            buf.writeInt(n);
            Class<?> com = cls.getComponentType();
            for (int i = 0; i < n; i++) {
                toRaw(buf, com, Array.get(obj, i));
            }
        } else if (cls.isEnum())
            buf.writeUtf(((Enum) obj).name());
        else if (MAP.containsKey(cls))
            MAP.get(cls).toPacket.accept(buf, obj);
        else
            toImpl(buf, cls, obj);

    }

}