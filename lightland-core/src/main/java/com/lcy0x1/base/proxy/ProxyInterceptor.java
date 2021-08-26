package com.lcy0x1.base.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ProxyInterceptor implements MethodInterceptor {
    private static final ThreadLocal<Object> HANDLE_DEQUE_THREAD_LOCAL = new ThreadLocal<>();
    private static final String onProxyName = "onProxy";
    private static final Class<?>[] onProxyParameterTypes = {Method.class, Object[].class, MethodProxy.class};
    private static final String getHandlerName = "getHandler";
    private static final Class<?>[] getHandlerParameterTypes = onProxyParameterTypes;

    private static boolean isOnProxyMethod(Method method) {
        return Reflections.equalsMethod(method, onProxyName, onProxyParameterTypes) ||
            Reflections.equalsMethod(method, getHandlerName, getHandlerParameterTypes);
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
            if (obj instanceof Proxy<?> && !isOnProxyMethod(method)) {
                final Result<?> result = ((Proxy<?>) obj).onProxy(method, args, proxy);
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