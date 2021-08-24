package com.lcy0x1.base.proxy.block;

import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerCache {
    public static final HandlerCache INSTANCE = new HandlerCache();

    interface OnProxy {
        BlockProxy.Result<?> onProxy(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable;
    }

    public static final OnProxy callSuper = (obj, method, args, proxy) -> BlockProxy.of(proxy.invokeSuper(obj, args));

    private Map<Method, OnProxy> handlerMap = new ConcurrentHashMap<>();

    @Nullable
    public OnProxy getHandler(@NotNull Method method) {
        return handlerMap.get(method);
    }

    public void setHandler(@NotNull Method method, @Nullable OnProxy onProxy) {
        if (onProxy == null) {
            onProxy = callSuper;
        }
        handlerMap.put(method, onProxy);
    }
}
