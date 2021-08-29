package com.lcy0x1.base.proxy.handler;

import com.lcy0x1.base.proxy.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyHandlerCache {
    public static final ProxyHandlerCache INSTANCE = new ProxyHandlerCache();
    public static final OnProxy callSuper = (obj, method, args, proxy) -> {
        Object result = proxy.invokeSuper(obj, args);
        if (result instanceof Result<?>) {
            result = ((Result<?>) result).snapshot();
        }
        return Result.of(result);
    };
    public static final OnProxy empty = (obj, method, args, proxy) -> Result.failed();
    private final Map<Method, OnProxy> handlerMap = new ConcurrentHashMap<>();


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
