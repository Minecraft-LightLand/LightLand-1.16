package com.lcy0x1.base.proxy.handler;

import com.lcy0x1.base.proxy.*;
import com.lcy0x1.base.proxy.annotation.WithinProxyContext;
import com.lcy0x1.base.proxy.container.WithinProxyContextConfig;
import lombok.Getter;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ProxyMethod extends ProxyHandler {
    Logger log = LogManager.getLogger(ProxyMethod.class);
    Map<CacheMapKey, Result<? extends ProxyHandler>> handlerCacheMap = new ConcurrentHashMap<>();

    @Override
    default Result<?> onProxy(@NotNull Proxy<?> obj, @NotNull Method method, @NotNull Object[] args, @NotNull MethodProxy proxy, @NotNull ProxyContext context) throws Throwable {
        final CacheMapKey key = CacheMapKey.of(method, getClass());
        final Result<? extends ProxyHandler> methodResult = handlerCacheMap.get(key);
        if (methodResult != null) {
            if (methodResult.isSuccess()) {
                return methodResult.getResult().onProxy(obj, method, args, proxy, context);
            } else {
                return Result.failed();
            }
        }
        ProxyHandler handler = getHandler(obj, method, args, proxy, context);
        if (handler == ProxyHandler.failed) {
            handlerCacheMap.put(key.snapshot(), Result.failed());
        } else {
            final WithinProxyContextConfig withinProxyContext = getProxyContextConfig();
            if (withinProxyContext != null && !(handler instanceof WithinProxyContextProxyHandler)) {
                handler = new WithinProxyContextProxyHandler(handler, withinProxyContext);
            }
            handlerCacheMap.put(key.snapshot(), Result.alloc(handler));
        }
        return handler.onProxy(obj, method, args, proxy, context);
    }

    default WithinProxyContextConfig getProxyContextConfig() {
        return WithinProxyContext.Utils.get(this);
    }

    @SuppressWarnings("unused")
    @NotNull
    default ProxyHandler getHandler(Object obj, Method method, Object[] args, MethodProxy proxy, ProxyContext context) throws Throwable {
        final Collection<? extends Class<?>> classes = context.get(ProxyContext.classes);
        if (classes != null && !classes.isEmpty() && classes.stream().noneMatch(c -> c.isInstance(this))) {
            return ProxyHandler.failed;
        }

        String methodName = context.get(ProxyContext.methodNameKey);
        if (StringUtils.isEmpty(methodName)) {
            methodName = method.getName();
        }

        //log.info("load method {} by {}", methodName, getClass());

        // get method by ReflectASM
        final MethodAccessGroup methodAccess = Reflections.getMethodAccessGroup(getClass());
        final MethodAccessGroup.MethodAccessIndex index = methodAccess.getIndex(methodName, method.getParameterTypes());
        //log.info("load MethodAccess index: {}", index);
        if (index != null) {
            ProxyHandler proxyHandler = (obj1, method1, args1, proxy1, context1) -> {
                Object invoke = index.invoke(this, args1);
                if (invoke instanceof Result<?>) {
                    invoke = ((Result<?>) invoke).snapshot();
                }
                return Result.of(invoke);
            };

            final WithinProxyContextConfig withinProxyContextConfig = WithinProxyContext.Utils.get(
                    Reflections.getMethod(getClass(), methodName, method.getParameterTypes()));
            if (withinProxyContextConfig != null) {
                proxyHandler = new WithinProxyContextProxyHandler(proxyHandler, withinProxyContextConfig);
            }

            return proxyHandler;
        }

        // get method by java reflect
        final Method declaredMethod = Reflections.getMethod(getClass(), methodName, method.getParameterTypes());
        if (declaredMethod != null) {
            declaredMethod.setAccessible(true);
            ProxyHandler proxyHandler = (obj1, method1, args1, proxy1, context1) -> {
                Object invoke = declaredMethod.invoke(this, args1);
                if (invoke instanceof Result<?>) {
                    invoke = ((Result<?>) invoke).snapshot();
                }
                return Result.of(invoke);
            };

            final WithinProxyContextConfig withinProxyContextConfig = WithinProxyContext.Utils.get(declaredMethod);
            if (withinProxyContextConfig != null) {
                proxyHandler = new WithinProxyContextProxyHandler(proxyHandler, withinProxyContextConfig);
            }

            return proxyHandler;
        }


        return ProxyHandler.failed;
    }

    @Getter
    final class CacheMapKey {
        @SuppressWarnings("ConstantConditions")
        private static final CacheMapKey main = new CacheMapKey(null, null);
        @NotNull
        private Method method;
        @NotNull
        private Class<? extends ProxyMethod> clazz;

        // hash cache
        private int hash;

        /**
         * 可以尝试从主线程中获取 CacheMapKey对象
         * 因为游戏的绝大部分逻辑都是在主线程中运行的
         * 这种优化的效果非常好，可以节约大量对象分配
         */
        public static CacheMapKey of(@NotNull Method method, @NotNull Class<? extends ProxyMethod> clazz) {
            if (Reflections.inMainThread()) {
                main.clazz = clazz;
                main.method = method;
                main.hash = 0;
                return main;
            } else {
                return new CacheMapKey(method, clazz);
            }
        }

        public CacheMapKey(@NotNull Method method, @NotNull Class<? extends ProxyMethod> clazz) {
            this.method = method;
            this.clazz = clazz;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CacheMapKey)) return false;
            CacheMapKey that = (CacheMapKey) o;
            return clazz == that.clazz && method == that.method;
        }

        @Override
        public int hashCode() {
            if (hash != 0) {
                hash = method.hashCode();
                hash = hash * 31 ^ clazz.hashCode();
                //hash = Objects.hash(method, clazz);
            }
            return hash;
        }

        public CacheMapKey snapshot() {
            if (Reflections.inMainThread()) {
                final CacheMapKey key = new CacheMapKey(method, clazz);
                key.hash = this.hash;
                return key;
            } else {
                return this;
            }
        }
    }
}