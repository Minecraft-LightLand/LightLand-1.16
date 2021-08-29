package com.lcy0x1.base.proxy;

import lombok.extern.log4j.Log4j2;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Log4j2
public class ProxyInterceptor implements MethodInterceptor {
    public static final ProxyInterceptor INSTANCE = new ProxyInterceptor();
    private static final MethodInterceptor[] CALLBACKS = {
            INSTANCE,
            (obj, method, args, proxy) -> proxy.invokeSuper(obj, args),
    };

    private static final String onProxyName = "onProxy";
    private static final Class<?>[] onProxyParameterTypes = {Method.class, Object[].class, MethodProxy.class};
    private static final String getHandlerName = "getHandler";
    private static final Class<?>[] getHandlerParameterTypes = onProxyParameterTypes;
    private static final ProxyCallbackFilter defaultProxyCallbackFilter = new ProxyCallbackFilter(Object.class, Proxy.class);

    @NotNull
    public static Enhancer getEnhancer(Class<? extends Proxy<?>> proxyClass) {
        return getEnhancer(proxyClass, defaultProxyCallbackFilter);
    }

    @NotNull
    public static Enhancer getEnhancer(Class<? extends Proxy<?>> proxyClass, Class<?>... ignoreClass) {
        return getEnhancer(proxyClass, new ProxyCallbackFilter(ignoreClass));
    }

    @NotNull
    public static Enhancer getEnhancer(Class<? extends Proxy<?>> proxyClass, CallbackFilter callbackFilter) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxyClass);
        enhancer.setCallbacks(CALLBACKS);
        if (callbackFilter != null) {
            enhancer.setCallbackFilter(callbackFilter);
        }
        return enhancer;
    }

    private static boolean isPassProxyMethod(Method method) {
        for (Method m : Proxy.class.getDeclaredMethods()) {
            if (!Modifier.isStatic(m.getModifiers()) && Reflections.equalsMethod(method, m)) {
                return true;
            }
        }
        return false;
        //return isOnProxyMethod(method) || isGetHandlerMethod(method);
    }

    private static boolean isOnProxyMethod(Method method) {
        return Reflections.equalsMethod(method, onProxyName, onProxyParameterTypes);
    }

    private static boolean isGetHandlerMethod(Method method) {
        return Reflections.equalsMethod(method, getHandlerName, getHandlerParameterTypes);
    }

    private ProxyInterceptor() {
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (obj instanceof Proxy<?>) {
            final Result<?> result = ((Proxy<?>) obj).onProxy(method, args, proxy);
            if (result != null && result.isSuccess()) {
                return result.getResult();
            }
        }
        return proxy.invokeSuper(obj, args);
    }
}