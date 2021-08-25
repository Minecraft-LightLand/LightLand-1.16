package com.lcy0x1.base.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Objects;

public class ProxyInterceptor implements MethodInterceptor {
    private static final ThreadLocal<ArrayDeque<Object>> HANDLE_DEQUE_THREAD_LOCAL = new ThreadLocal<>();
    private static final Class<?>[] parameterTypes = {Method.class, Object[].class, MethodProxy.class};
    private static final Field parameterTypesField;

    static {
        try {
            parameterTypesField = Method.class.getDeclaredField("parameterTypes");
            parameterTypesField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isOnProxyMethod(Method method) {
        try {
            return Objects.equals(method.getName(), "onProxy") &&
                    Arrays.equals(parameterTypes, (Class<?>[]) parameterTypesField.get(method));
        } catch (IllegalAccessException e) {
            return true;
        }
    }

    private static ArrayDeque<Object> getHandleDeque() {
        ArrayDeque<Object> objectArrayDeque = HANDLE_DEQUE_THREAD_LOCAL.get();
        if (objectArrayDeque == null) {
            objectArrayDeque = new ArrayDeque<>();
            HANDLE_DEQUE_THREAD_LOCAL.set(objectArrayDeque);
        }
        return objectArrayDeque;
    }

    public static <T> T getHandle() {
        //noinspection unchecked
        return (T) getHandleDeque().getFirst();
    }

    public static <T> T getHandle(T type) {
        return getHandle();
    }

    private static void push(Object obj) {
        getHandleDeque().push(obj);
    }

    private static void pop() {
        getHandleDeque().pop();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        push(obj);
        try {
            if (obj instanceof ProxyContainer<?> && !isOnProxyMethod(method)) {
                final Proxy.Result<?> result = ((ProxyContainer<?>) obj).onProxy(method, args, proxy);
                if (result != null && result.isSuccess()) {
                    return result.getResult();
                }
            }
            return proxy.invokeSuper(obj, args);
        } finally {
            pop();
        }
    }
}