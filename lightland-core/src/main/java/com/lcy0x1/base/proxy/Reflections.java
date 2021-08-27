package com.lcy0x1.base.proxy;

import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Reflections {
    private static final Field arrayListElementDataField = getField(ArrayList.class, "elementData");
    private static final Field parameterTypesField = getField(Method.class, "parameterTypes");
    private static final Map<Class<?>, Result<MethodAccess>> methodAccessMap = new ConcurrentHashMap<>();
    private static final Thread mainThread = Thread.currentThread();
    private static UnsafeReflections unsafe = null;

    static {
        try {
            unsafe = new UnsafeReflections();
        } catch (Throwable ignored) {
        }
    }

    @Data
    @AllArgsConstructor
    public static class Reference<T> {
        T value;
    }

    public static boolean inMainThread() {
        return Thread.currentThread() == mainThread;
    }

    public static Object[] getElementData(ArrayList<?> arrayList) throws IllegalAccessException {
        if (unsafe != null) {
            return unsafe.getElementData(arrayList);
        }
        if (arrayListElementDataField != null) {
            return (Object[]) arrayListElementDataField.get(arrayList);
        } else {
            return null;
        }
    }

    public static Class<?>[] getParameterTypes(Method method) {
        if (unsafe != null) {
            return unsafe.getParameterTypes(method);
        }
        if (parameterTypesField == null) {
            return null;
        }
        try {
            return (Class<?>[]) parameterTypesField.get(method);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static boolean equalsMethod(Method method, String name, Class<?>[] parameterTypes) {
        return Objects.equals(method.getName(), name) &&
            Arrays.equals(parameterTypes, getParameterTypes(method));
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        while (clazz != Object.class) {
            try {
                final Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public static Object getField(@Nullable Field field, Object receiver) {
        if (field == null) {
            return null;
        }
        try {
            return field.get(receiver);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    public static MethodAccess getMethodAccess(Class<?> clazz) {
        if (clazz.getName().indexOf('/') == -1) {
            return getMethodAccessWithCache(clazz);
        }
        // clazz is lambda
        final Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            return getMethodAccessWithCache(interfaces[0]);
        }
        return getMethodAccess(clazz.getSuperclass());
    }

    private static MethodAccess getMethodAccessWithCache(Class<?> clazz) {
        final Result<MethodAccess> methodAccessResult = methodAccessMap.get(clazz);
        if (methodAccessResult != null) {
            return methodAccessResult.getResult();
        }

        MethodAccess methodAccess = null;
        try {
            methodAccess = MethodAccess.get(clazz);
            methodAccessMap.put(clazz, Result.alloc(methodAccess));
        } catch (Exception ignored) {
            methodAccessMap.put(clazz, Result.failed());
        }
        return methodAccess;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object value) {
        return (T) value;
    }

    public static class UnsafeReflections {
        private final sun.misc.Unsafe theUnsafe = (sun.misc.Unsafe) getField(getField(sun.misc.Unsafe.class, "theUnsafe"), null);
        private final long parameterTypesOffset = objectFieldOffset(parameterTypesField);
        private final long arrayListElementDataOffset = objectFieldOffset(arrayListElementDataField);

        public long objectFieldOffset(@Nullable Field field) {
            if (field == null) {
                return -1;
            }
            return theUnsafe.objectFieldOffset(field);
        }

        public Class<?>[] getParameterTypes(Method method) {
            if (parameterTypesOffset < 0) return null;
            return (Class<?>[]) theUnsafe.getObject(method, parameterTypesOffset);
        }

        public Object[] getElementData(ArrayList<?> arrayList) {
            if (arrayListElementDataOffset < 0) return null;
            return (Object[]) theUnsafe.getObject(arrayList, arrayListElementDataOffset);
        }
    }
}