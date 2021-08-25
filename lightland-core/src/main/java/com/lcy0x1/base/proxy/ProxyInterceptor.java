package com.lcy0x1.base.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class ProxyInterceptor implements MethodInterceptor {
    private static final ThreadLocal<Object> HANDLE_DEQUE_THREAD_LOCAL = new ThreadLocal<>();
    private static final String onProxyName = "onProxy";
    private static final Class<?>[] onProxyParameterTypes = {Method.class, Object[].class, MethodProxy.class};
    private static final String getHandlerName = "getHandler";
    private static final Class<?>[] getHandlerParameterTypes = onProxyParameterTypes;
    private static final Field parameterTypesField;

    static {
        try {
            parameterTypesField = Method.class.getDeclaredField("parameterTypes");
            parameterTypesField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?>[] getParameterTypes(Method method) {
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

    private static boolean isOnProxyMethod(Method method) {
        return equalsMethod(method, onProxyName, onProxyParameterTypes) ||
                equalsMethod(method, getHandlerName, getHandlerParameterTypes);
    }


    public static <T> T getHandle() {
        //noinspection unchecked
        return (T) HANDLE_DEQUE_THREAD_LOCAL.get();
    }

    public static <T> T getHandle(T type) {
        return getHandle();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        final Object parent = HANDLE_DEQUE_THREAD_LOCAL.get();
        HANDLE_DEQUE_THREAD_LOCAL.set(obj);
        try {
            if (obj instanceof ProxyContainer<?> && !isOnProxyMethod(method)) {
                final Proxy.Result<?> result = ((ProxyContainer<?>) obj).onProxy(method, args, proxy);
                if (result != null && result.isSuccess()) {
                    return result.getResult();
                }
            }
            return proxy.invokeSuper(obj, args);
        } finally {
            HANDLE_DEQUE_THREAD_LOCAL.set(parent);
        }
    }
}