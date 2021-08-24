package com.lcy0x1.base.proxy;

import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyContainerHandlerCache {
    public static final ProxyContainerHandlerCache INSTANCE = new ProxyContainerHandlerCache();
    public static final OnProxy callSuper = (obj, method, args, proxy) -> Proxy.of(proxy.invokeSuper(obj, args));
    public static final OnProxy empty = (obj, method, args, proxy) -> Proxy.failed();
    private final Map<Method, OnProxy> handlerMap = new ConcurrentHashMap<>();

    public interface OnProxy {
        Proxy.Result<?> onProxy(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable;
    }

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
