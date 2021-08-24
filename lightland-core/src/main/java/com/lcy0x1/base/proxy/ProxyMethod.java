package com.lcy0x1.base.proxy;

import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ProxyMethod {
    Map<Class<? extends ProxyMethod>, Map<Method, Proxy.Result<Method>>> handlerCacheMapMap = new HashMap<>();

    @NotNull
    static Map<Method, Proxy.Result<Method>> getHandlerCacheMap(@NotNull Class<? extends ProxyMethod> type) {
        Map<Method, Proxy.Result<Method>> handlerCacheMap = handlerCacheMapMap.get(type);
        if (handlerCacheMap == null) synchronized (handlerCacheMapMap) {
            handlerCacheMap = handlerCacheMapMap.get(type);
            if (handlerCacheMap == null) {
                handlerCacheMap = new ConcurrentHashMap<>();
                handlerCacheMapMap.put(type, handlerCacheMap);
            }
        }
        return handlerCacheMap;
    }

    default Proxy.Result<?> onProxy(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        final Method selfMethod;
        final Map<Method, Proxy.Result<Method>> handlerCacheMap = getHandlerCacheMap(getClass());
        final Proxy.Result<Method> methodResult = handlerCacheMap.get(method);
        if (methodResult != null) {
            if (methodResult.isSuccess()) {
                return Proxy.of(methodResult.getResult().invoke(this, args));
            } else {
                return Proxy.failed();
            }
        }
        try {
            selfMethod = getClass().getMethod(method.getName(), method.getParameterTypes());
            selfMethod.setAccessible(true);
            handlerCacheMap.put(method, new Proxy.Result<>(true, selfMethod));
        } catch (Exception e) {
            handlerCacheMap.put(method, Proxy.failed());
            return Proxy.failed();
        }
        return Proxy.of(selfMethod.invoke(this, args));
    }
}