package com.lcy0x1.base.proxy;

import com.lcy0x1.base.proxy.handler.ProxyMethod;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyContext {
    private static final AtomicInteger keyIdGenerator = new AtomicInteger();
    private static final ThreadLocal<ProxyContext> localProxyContext = new ThreadLocal<>();

    public static final Key<String> methodNameKey = new Key<>();
    public static final Key<String> block = new Key<>();
    public static final Key<Result<ProxyMethod>> proxyMethod = new Key<>();
    public static final Key<Boolean> cacheFirstProxyMethod = new Key<>();
    public static final Key<Collection<? extends Class<?>>> classes = new Key<>();

    @Data
    public static class Key<T> {
        private final int id = keyIdGenerator.getAndIncrement();
    }

    public interface Callable<R> {
        R call() throws Throwable;
    }

    public static <R> R withThreadLocalProxyContext(ProxyContext context, Callable<R> c) throws Throwable {
        final ProxyContext parent = localProxyContext.get();
        localProxyContext.set(context);
        try {
            return c.call();
        } finally {
            localProxyContext.set(parent);
        }
    }

    public static ProxyContext getLocalProxyContext() {
        return localProxyContext.get();
    }

    @Nullable
    private ProxyContext parent = null;
    @Nullable
    private Object[] context = null;

    public ProxyContext() {
        parent = null;
    }

    private ProxyContext(@Nullable Object[] context) {
        this.context = context;
    }

    public ProxyContext(@Nullable ProxyContext parent) {
        this.parent = parent;
    }

    @NotNull
    public ProxyContext getSubContext() {
        return new ProxyContext(this);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T get(@Nullable Key<T> key) {
        if (key == null || context == null || key.getId() >= context.length) {
            return null;
        }

        T t = (T) context[key.getId()];
        if (t == null && parent != null) {
            t = parent.get(key);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getAndRemove(@Nullable Key<T> key) {
        if (key == null) {
            return null;
        }
        T t;
        if (context == null || key.getId() >= context.length) {
            t = null;
        } else {
            t = (T) context[key.getId()];
        }

        if (t == null && parent != null) {
            t = parent.getAndRemove(key);
        } else if (context != null) {
            context[key.getId()] = null;
        }
        return t;
    }

    @SuppressWarnings("ConstantConditions")
    public <T> void put(@NotNull Key<T> key, T value) {
        if (key == null) {
            return;
        }
        if (context == null) {
            context = new Object[keyIdGenerator.get()];
        } else if (key.getId() >= context.length) {
            context = Arrays.copyOf(context, keyIdGenerator.get());
        }
        context[key.getId()] = value;
    }

    public void clean() {
        context = null;
    }

    public ProxyContext snapshot() {
        if (context != null) {
            return new ProxyContext(putContext(new Object[context.length]));
        }
        if (parent != null) {
            return parent.snapshot();
        }
        return new ProxyContext();
    }

    private Object[] putContext(Object[] target) {
        if (parent != null) {
            parent.putContext(target);
        }
        if (context != null) {
            System.arraycopy(context, 0, target, 0, Math.min(context.length, target.length));
        }
        return target;
    }
}
