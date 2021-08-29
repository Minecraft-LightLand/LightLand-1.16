package com.lcy0x1.base.proxy;

import com.lcy0x1.base.proxy.handler.ProxyMethod;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import net.minecraft.block.Block;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用来储存代理逻辑的上下文对象的容器
 * 线程不安全，不支持在不同线程间共用同一个对象
 * 使用数组储存数据，每个key保存一个index
 * 获取和设置值的方式实际上就是用key的index访问数组元素，时间复杂度为 O(1)，比HashMap还快
 * <p>
 * 为了隔离每一次代理的环境，同时也为了减少数组拷贝开销，允许ProxyContext拥有父环境
 * 当在子环境中找不到数据的时候就会去访问父环境获取
 * 而设置值的时候只能更改子环境的值
 * 具体实现使用了 CopyOnWrite 技术，子环境在初始化之后只有在第一次更改值的时候才会创建数组
 */
@Log4j2
public class ProxyContext {
    private static final AtomicInteger keyIdGenerator = new AtomicInteger();
    private static final ThreadLocal<ProxyContext> localProxyContext = new ThreadLocal<>();
    // 主线程特化的 ProxyContext
    private static ProxyContext mainProxyContext = null;

    /**
     * 用来存储当前代理目标方法名设置的key
     */
    public static final Key<String> methodNameKey = new Key<>();
    public static final Key<Proxy<?>> proxy = new Key<>();
    public static final Key<Result<ProxyMethod>> proxyMethod = new Key<>();
    public static final Key<Boolean> cacheFirstProxyMethod = new Key<>();
    public static final Key<Collection<? extends Class<?>>> classes = new Key<>();
    public static final Key<Result<?>> pre = new ProxyContext.Key<>(); // pre proxy return
    public static final Key<Result<Object>> objectPre = new ProxyContext.Key<>(pre); // pre proxy return
    public static final Key<Logger> loggerKey = new Key<>();
    public static final Key<Boolean> continueFirstProxyMethod = new Key<>();

    public static final Key<Block> block = new Key<>(proxy, Block.class);

    @Data
    public static class Key<T> {
        private final int id;
        // 允许通过 Class 过滤获取的值
        private final Class<T> clazz;

        public Key() {
            id = keyIdGenerator.getAndIncrement();
            clazz = null;
        }

        public Key(@NotNull Key<?> key) {
            this.id = key.id;
            clazz = null;
        }

        public Key(@Nullable Class<T> clazz) {
            id = keyIdGenerator.getAndIncrement();
            this.clazz = clazz;
        }

        public Key(@NotNull Key<?> key, @Nullable Class<T> clazz) {
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

        public boolean put(T value) {
            final ProxyContext local = local();
            if (local != null) {
                local.put(id, value);
                return true;
            } else {
                return false;
            }
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

    @Nullable
    public static ProxyContext local() {
        if (Reflections.inMainThread()) {
            return mainProxyContext;
        } else {
            return localProxyContext.get();
        }
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
        if (key == null) {
            return null;
        }
        return key.get(this);
    }

    @Nullable
    public <T> T getResult(@Nullable Key<Result<T>> key) {
        if (key == null) {
            return null;
        }
        final Result<T> result = key.get(this);
        if (result != null && result.isSuccess()) {
            return result.getResult();
        } else {
            return null;
        }
    }

    public <T> void setResult(@Nullable Key<Result<T>> key, T result) {
        if (key == null) {
            return;
        }
        set(key, Result.alloc(result));
    }

    public <T> void putResult(@Nullable Key<Result<T>> key, T result) {
        if (key == null) {
            return;
        }
        put(key, Result.alloc(result));
    }

    @Nullable
    public <T> T getAndRemove(@Nullable Key<T> key) {
        if (key == null) {
            return null;
        }
        T t = key.get(this);
        if (t != null) {
            key.remove(this);
        }
        return t;
    }

    @SuppressWarnings("ConstantConditions")
    public void remove(@NotNull Key<?> key) {
        if (key == null) {
            return;
        }
        key.remove(this);
    }

    public <T> void set(@NotNull Key<T> key, T value) {
        put(key, value);
    }

    @SuppressWarnings("ConstantConditions")
    public <T> void put(@NotNull Key<T> key, T value) {
        if (key == null) {
            return;
        }
        key.put(this, value);
    }

    /**
     * 根据 Key id 获取代理环境变量
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T get(int id) {
        T t = null;
        ProxyContext proxyContext = this;
        do {
            // 获取环境
            final Object[] context = proxyContext.context;
            // 判断越界
            if (context != null && id < context.length) {
                t = (T) context[id];
            }
            // 继续循环
        } while (t == null && (proxyContext = proxyContext.parent) != null);
        return t;
    }

    /**
     * 根据 Key id 设置代理环境变量
     */
    public <T> void put(int id, T value) {
        if (context == null) {
            // context 为空，生成新环境
            context = new Object[getResize(keyIdGenerator.get())];
            // 如果有父环境，从父环境拷贝一份镜像以加速获取
            if (parent != null && parent.context != null) {
                System.arraycopy(parent.context, 0, context, 0, parent.context.length);
            }
        } else if (id >= context.length) {
            context = Arrays.copyOf(context, getResize(keyIdGenerator.get()));
        }
        context[id] = value;
    }

    public void remove(int id) {
        if (context == null || id >= context.length) {
            return;
        }
        context[id] = null;
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

    @Override
    public String toString() {
        return "ProxyContext{" +
                "parent=" + parent +
                ", context=" + Arrays.toString(context) +
                '}';
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
