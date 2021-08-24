package com.lcy0x1.base.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ProxyInterceptor implements MethodInterceptor {
    private static final ThreadLocal<Object> handle = new ThreadLocal<>();

    public static <T> T getHandle() {
        //noinspection unchecked
        return (T) handle.get();
    }

    public static <T> T getHandle(T type) {
        //noinspection unchecked
        return (T) handle.get();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        handle.set(obj);
        try {
            if (obj instanceof ProxyContainer<?>) {
                final Proxy.Result<?> result = ((ProxyContainer<?>) obj).onProxy(obj, method, args, proxy);
                if (result != null && result.isSuccess()) {
                    return result.getResult();
                }
            }
            return proxy.invokeSuper(obj, args);
        } finally {
            handle.remove();
        }
    }
}