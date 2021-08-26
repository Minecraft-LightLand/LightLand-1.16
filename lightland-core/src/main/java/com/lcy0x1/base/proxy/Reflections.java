package com.lcy0x1.base.proxy;

import com.esotericsoftware.reflectasm.MethodAccess;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class Reflections {
    private static final Field parameterTypesField = getField(Method.class, "parameterTypes");

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
            return MethodAccess.get(clazz);
        }
        // clazz is lambda
        final Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            return MethodAccess.get(interfaces[0]);
        }
        return getMethodAccess(clazz.getSuperclass());
    }
}
