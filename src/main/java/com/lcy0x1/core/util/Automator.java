package com.lcy0x1.core.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Capable of handing primitive types, array, BlockPos, ItemStack, inheritance
 * <br>
 * Not capable of handing collections
 */
public class Automator {

    private static class ClassHandler<R extends INBT, T> {

        private final Function<INBT, ?> fromTag;
        private final Function<Object, INBT> toTag;

        @SuppressWarnings("unchecked")
        private ClassHandler(Class<T> cls, Function<R, T> ft, Function<T, INBT> tt, Class<?>... alt) {
            fromTag = (Function<INBT, ?>) ft;
            toTag = (Function<Object, INBT>) tt;
            MAP.put(cls, this);
            for (Class<?> c : alt)
                MAP.put(c, this);
        }

    }

    private static final Map<Class<?>, ClassHandler<?, ?>> MAP = new HashMap<>();

    static {
        new ClassHandler<>(Long.class, LongNBT::getAsLong, LongNBT::valueOf, long.class);
        new ClassHandler<>(Integer.class, IntNBT::getAsInt, IntNBT::valueOf, int.class);
        new ClassHandler<>(Short.class, ShortNBT::getAsShort, ShortNBT::valueOf, short.class);
        new ClassHandler<>(Byte.class, ByteNBT::getAsByte, ByteNBT::valueOf, byte.class);
        new ClassHandler<ByteNBT, Boolean>(Boolean.class, tag -> tag.getAsByte() != 0, ByteNBT::valueOf, boolean.class);
        new ClassHandler<>(Float.class, FloatNBT::getAsFloat, FloatNBT::valueOf, float.class);
        new ClassHandler<>(Double.class, DoubleNBT::getAsDouble, DoubleNBT::valueOf, double.class);
        new ClassHandler<>(long[].class, LongArrayNBT::getAsLongArray, LongArrayNBT::new);
        new ClassHandler<>(int[].class, IntArrayNBT::getAsIntArray, IntArrayNBT::new);
        new ClassHandler<>(byte[].class, ByteArrayNBT::getAsByteArray, ByteArrayNBT::new);
        new ClassHandler<StringNBT, String>(String.class, INBT::getAsString, StringNBT::valueOf);
        new ClassHandler<>(ItemStack.class, ItemStack::of, is -> is.save(new CompoundNBT()));
        new ClassHandler<CompoundNBT, BlockPos>(BlockPos.class, tag -> new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")), obj -> {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt("x", obj.getX());
            tag.putInt("y", obj.getY());
            tag.putInt("z", obj.getZ());
            return tag;
        });
        new ClassHandler<IntArrayNBT, UUID>(UUID.class, NBTUtil::loadUUID, NBTUtil::createUUID);
        new ClassHandler<CompoundNBT, CompoundNBT>(CompoundNBT.class, e -> e, e -> e);
    }

    public static Object fromTag(CompoundNBT tag, Class<?> cls, Object obj, Predicate<SerialClass.SerialField> pred)
            throws Exception {
        if (obj == null)
            obj = cls.newInstance();
        while (cls.getAnnotation(SerialClass.class) != null) {
            for (Field f : cls.getDeclaredFields()) {
                if (!pred.test(f.getAnnotation(SerialClass.SerialField.class)))
                    continue;
                Object fa = fromTagRaw(tag.get(f.getName()), f.getType(), pred);
                if (fa != null || !f.getType().isPrimitive())
                    f.set(obj, fa);
            }
            cls = cls.getSuperclass();
        }
        return obj;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object fromTagRaw(INBT tag, Class<?> cls, Predicate<SerialClass.SerialField> pred) throws Exception {
        if (tag == null)
            if (cls == ItemStack.class)
                return ItemStack.EMPTY;
            else
                return null;
        if (MAP.containsKey(cls))
            return MAP.get(cls).fromTag.apply(tag);
        if (cls.isArray()) {
            ListNBT list = (ListNBT) tag;
            int n = list.size();
            Class<?> com = cls.getComponentType();
            Object ans = Array.newInstance(com, n);
            for (int i = 0; i < n; i++) {
                Array.set(ans, i, fromTagRaw(list.get(i), com, pred));
            }
            return ans;
        }
        if (cls.isEnum()) {
            return Enum.valueOf((Class) cls, tag.getAsString());
        }
        if (cls.getAnnotation(SerialClass.class) != null)
            return fromTag((CompoundNBT) tag, cls, null, pred);
        throw new Exception("unsupported class " + cls);
    }

    public static CompoundNBT toTag(CompoundNBT tag, Class<?> cls, Object obj, Predicate<SerialClass.SerialField> pred)
            throws Exception {
        if (obj == null)
            return tag;
        while (cls.getAnnotation(SerialClass.class) != null) {
            for (Field f : cls.getDeclaredFields()) {
                if (!pred.test(f.getAnnotation(SerialClass.SerialField.class)))
                    continue;
                if (f.get(obj) != null)
                    tag.put(f.getName(), toTagRaw(f.getType(), f.get(obj), pred));
            }
            cls = cls.getSuperclass();
        }
        return tag;
    }

    public static INBT toTagRaw(Class<?> cls, Object obj, Predicate<SerialClass.SerialField> pred) throws Exception {
        if (MAP.containsKey(cls))
            return MAP.get(cls).toTag.apply(obj);
        if (cls.isArray()) {
            ListNBT list = new ListNBT();
            int n = Array.getLength(obj);
            Class<?> com = cls.getComponentType();
            for (int i = 0; i < n; i++) {
                list.add(toTagRaw(com, Array.get(obj, i), pred));
            }
            return list;
        }
        if (cls.isEnum())
            return StringNBT.valueOf(((Enum<?>) obj).name());
        if (cls.getAnnotation(SerialClass.class) != null)
            return toTag(new CompoundNBT(), cls, obj, pred);
        throw new Exception("unsupported class " + cls);
    }

}