package com.lcy0x1.base.proxy;

import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ProxyInterceptor implements MethodInterceptor {
    public static final ProxyInterceptor INSTANCE = new ProxyInterceptor();
    private static final MethodInterceptor[] CALLBACKS = {
        (obj, method, args, proxy) -> proxy.invokeSuper(obj, args),
        INSTANCE,
    };
    private static final CallbackFilter callbackFilter = method -> {
        if (isPassProxyMethod(method)) {
            return 0;
        } else {
            return 1;
        }
    };

    private static final ThreadLocal<Object> HANDLE_DEQUE_THREAD_LOCAL = new ThreadLocal<>();
    private static Object HANDLE_DEQUE;
    private static final String onProxyName = "onProxy";
    private static final Class<?>[] onProxyParameterTypes = {Method.class, Object[].class, MethodProxy.class};
    private static final String getHandlerName = "getHandler";
    private static final Class<?>[] getHandlerParameterTypes = onProxyParameterTypes;

    public static Enhancer getEnhancer(Class<? extends Proxy<?>> proxyClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxyClass);
        enhancer.setCallbacks(CALLBACKS);
        enhancer.setCallbackFilter(callbackFilter);
        return enhancer;
    }

    private static boolean isPassProxyMethod(Method method) {
        return isOnProxyMethod(method) || isGetHandlerMethod(method);
    }

    private static boolean isOnProxyMethod(Method method) {
        return Reflections.equalsMethod(method, onProxyName, onProxyParameterTypes);
    }

    private static boolean isGetHandlerMethod(Method method) {
        return Reflections.equalsMethod(method, getHandlerName, getHandlerParameterTypes);
    }

    public static <T> T getHandle() {
        //noinspection unchecked
        return (T) HANDLE_DEQUE_THREAD_LOCAL.get();
    }

    public static <T> T getHandle(T type) {
        return getHandle();
    }

    private ProxyInterceptor() {
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        final Object parent;
        final boolean inMainThread = Reflections.inMainThread();
        if (inMainThread) {
            parent = HANDLE_DEQUE;
            HANDLE_DEQUE = obj;
        } else {
            parent = HANDLE_DEQUE_THREAD_LOCAL.get();
            HANDLE_DEQUE_THREAD_LOCAL.set(obj);
        }
        try {
            if (obj instanceof Proxy<?>) {
                final Result<?> result = ((Proxy<?>) obj).onProxy(method, args, proxy);
                if (result != null && result.isSuccess()) {
                    return result.getResult();
                }
            }
            return proxy.invokeSuper(obj, args);
        } finally {
            if (inMainThread) {
                HANDLE_DEQUE = parent;
            } else {
                HANDLE_DEQUE_THREAD_LOCAL.set(parent);
            }
        }
    }
}