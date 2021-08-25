package com.lcy0x1.base.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyContext {
    public static final Key<String> methodNameKey = new Key<>();
    public static final Key<String> block = new Key<>();
    public static final Key<Proxy.Result<ProxyMethod>> proxyMethod = new Key<>();
    private final ProxyContext parent;
    private final Map<Key<?>, Object> context = new ConcurrentHashMap<>();

    public ProxyContext() {
        parent = null;
    }

    public ProxyContext(ProxyContext parent) {
        this.parent = parent;
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

    public <T> void put(@NotNull Key<T> key, T value) {
        if (key == null) {
            return;
        }
        context.put(key, value);
    }

    public static class Key<T> {
    }
}
