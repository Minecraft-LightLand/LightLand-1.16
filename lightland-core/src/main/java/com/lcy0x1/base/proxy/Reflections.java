package com.lcy0x1.base.proxy;

import com.esotericsoftware.reflectasm.MethodAccess;

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

    public static Object[] getElementData(ArrayList<?> arrayList) throws IllegalAccessException {
        if (arrayListElementDataField != null) {
            return (Object[]) arrayListElementDataField.get(arrayList);
        } else {
            return null;
        }
    }

    public static Class<?>[] getParameterTypes(Method method) {
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
}
