package com.lcy0x1.base.proxy.block;

import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface Proxy {
    Map<Class<? extends Proxy>, Map<Method, BlockProxy.Result<Method>>> handlerCacheMapMap = new HashMap<>();

    @NotNull
    static Map<Method, BlockProxy.Result<Method>> getHandlerCacheMap(@NotNull Class<? extends Proxy> type) {
        Map<Method, BlockProxy.Result<Method>> handlerCacheMap = handlerCacheMapMap.get(type);
        if (handlerCacheMap == null) synchronized (handlerCacheMapMap) {
            handlerCacheMap = handlerCacheMapMap.get(type);
            if (handlerCacheMap == null) {
                handlerCacheMap = new ConcurrentHashMap<>();
                handlerCacheMapMap.put(type, handlerCacheMap);
            }
        }
        return handlerCacheMap;
    }

    default BlockProxy.Result<?> onProxy(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        final Method selfMethod;
        final Map<Method, BlockProxy.Result<Method>> handlerCacheMap = getHandlerCacheMap(getClass());
        final BlockProxy.Result<Method> methodResult = handlerCacheMap.get(method);
        if (methodResult != null) {
            if (methodResult.isSuccess()) {
                return BlockProxy.of(methodResult.getResult().invoke(this, args));
            } else {
                return BlockProxy.failed();
            }
        }
        try {
            selfMethod = getClass().getMethod(method.getName(), method.getParameterTypes());
            handlerCacheMap.put(method, new BlockProxy.Result<>(true, selfMethod));
        } catch (Exception e) {
            handlerCacheMap.put(method, BlockProxy.failed());
            return BlockProxy.failed();
        }
        return BlockProxy.of(selfMethod.invoke(this, args));
    }
}