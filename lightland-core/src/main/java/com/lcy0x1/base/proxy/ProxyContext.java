package com.lcy0x1.base.proxy;

import com.lcy0x1.base.proxy.handler.ProxyHandler;
import lombok.Data;
import net.minecraft.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyContext {
    private static final AtomicInteger keyIdGenerator = new AtomicInteger();
    private static final ThreadLocal<ProxyContext> localProxyContext = new ThreadLocal<>();
    private static ProxyContext mainProxyContext = null;

    public static final Key<String> methodNameKey = new Key<>();
    public static final Key<Proxy<?>> proxy = new Key<>();
    public static final Key<Result<ProxyHandler>> proxyMethod = new Key<>();
    public static final Key<Boolean> cacheFirstProxyMethod = new Key<>();
    public static final Key<Collection<? extends Class<?>>> classes = new Key<>();
    public static final Key<Object> pre = new ProxyContext.Key<>(); // pre proxy return

    public static final Key<Block> block = new Key<Block>(proxy) {
        @Override
        public Block get(ProxyContext context) {
            final Object value = context.get(getId());
            if (value instanceof Block) {
                return (Block) value;
            } else {
                return null;
            }
        }
    };

    @Data
    public static class Key<T> {
        private final int id;
        private final Class<T> clazz;

        public Key() {
            id = keyIdGenerator.getAndIncrement();
            clazz = null;
        }

        public Key(Key<?> key) {
            this.id = key.id;
            clazz = null;
        }

        public Key(Key<?> key, Class<T> clazz) {
            this.id = key.id;
            this.clazz = clazz;
        }

        @SuppressWarnings("unchecked")
        public T get(ProxyContext context) {
            final Object value = context.get(id);
            if (clazz == null || clazz.isInstance(value)) {
                return (T) value;
            } else {
                return null;
            }
        }

        public void remove(ProxyContext context) {
            context.remove(id);
        }

        public void put(ProxyContext context, T value) {
            context.put(id, value);
        }
    }

    public interface Callable<R> {
        R call() throws Throwable;
    }

    public static <R> R withThreadLocalProxyContext(ProxyContext context, Callable<R> c) throws Throwable {
        if (Reflections.inMainThread()) {
            final ProxyContext parent = mainProxyContext;
            mainProxyContext = context;
            try {
                return c.call();
            } finally {
                mainProxyContext = parent;
            }
        } else {
            final ProxyContext parent = localProxyContext.get();
            localProxyContext.set(context);
            try {
                return c.call();
            } finally {
                localProxyContext.set(parent);
            }
        }
    }

    public static ProxyContext local() {
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

    @Nullable
    public <T> T get(@Nullable Key<T> key) {
        if (key == null || context == null) {
            return null;
        }
        return key.get(this);
    }

    @Nullable
    public <T> T getAndRemove(@Nullable Key<T> key) {
        if (key == null || context == null) {
            return null;
        }
        T t = key.get(this);
        if (t != null) {
            key.remove(this);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T get(int id) {
        if (context == null || id >= context.length) {
            return null;
        }

        T t = (T) context[id];
        if (t == null && parent != null) {
            t = parent.get(id);
        }
        return t;
    }

    public void remove(int id) {
        if (context == null || id >= context.length) {
            return;
        }
        context[id] = null;
    }

    @SuppressWarnings("ConstantConditions")
    public <T> void put(@NotNull Key<T> key, T value) {
        if (key == null) {
            return;
        }
        key.put(this, value);
    }

    public <T> void put(int id, T value) {
        if (context == null) {
            context = new Object[getResize(keyIdGenerator.get())];
        } else if (id >= context.length) {
            context = Arrays.copyOf(context, getResize(keyIdGenerator.get()));
        }
        context[id] = value;
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

    private static int getResize(int size) {
        int k = 0;
        if (size >> (k ^ 16) != 0) k = k ^ 16;
        if (size >> (k ^ 8) != 0) k = k ^ 8;
        if (size >> (k ^ 4) != 0) k = k ^ 4;
        if (size >> (k ^ 2) != 0) k = k ^ 2;
        if (size >> (k ^ 1) != 0) k = k ^ 1;
        return 1 << k + 1;
    }
}
