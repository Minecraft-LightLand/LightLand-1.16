package com.lcy0x1.base.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyContext {
    public static final Key<String> methodNameKey = new Key<>();
    public static final Key<String> block = new Key<>();
    public static final Key<Result<ProxyMethod>> proxyMethod = new Key<>();
    public static final Key<Boolean> nextProxyMethod = new Key<>();
    public static final Key<Boolean> cacheFirstProxyMethod = new Key<>();
    private final ProxyContext parent;
    private final Map<Key<?>, Object> context = new ConcurrentHashMap<>();
    private final ThreadLocal<ProxyContext> subContextThreadLocal = new ThreadLocal<>();

    public ProxyContext() {
        parent = null;
    }

    public ProxyContext(ProxyContext parent) {
        this.parent = parent;
    }

    @NotNull
    public ProxyContext getSubContext() {
        ProxyContext subContext = subContextThreadLocal.get();
        if (subContext == null) {
            subContext = new ProxyContext(this);
            subContextThreadLocal.set(subContext);
        }

        subContext.context.clear();

        return subContext;
    }

    @Nullable
    public <T> T get(@Nullable Key<T> key) {
        if (key == null) {
            return null;
        }
        //noinspection unchecked

        T t = (T) context.get(key);
        if (t == null && parent != null) {
            t = parent.get(key);
        }
        return t;
    }

    @Nullable
    public <T> T getAndRemove(@Nullable Key<T> key) {
        if (key == null) {
            return null;
        }
        //noinspection unchecked

        T t = (T) context.get(key);
        if (t == null && parent != null) {
            t = parent.getAndRemove(key);
        } else {
            context.remove(key);
        }
        return t;
    }

    @SuppressWarnings("ConstantConditions")
    public <T> void put(@NotNull Key<T> key, T value) {
        if (key == null) {
            return;
        }
        context.put(key, value);
    }

    public static class Key<T> {
    }
}
