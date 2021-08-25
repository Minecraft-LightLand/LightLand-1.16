package com.lcy0x1.base.proxy;

import lombok.Data;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public interface ProxyMethod extends ProxyMethodHandler {
    ProxyMethod failed = new ProxyMethod() {
        @Override
        public Proxy.Result<?> onProxy(Object obj, Method method, Object[] args, MethodProxy proxy, ProxyContext context) throws Throwable {
            return Proxy.failed();
        }
    };
    Map<CacheMapKey, Proxy.Result<? extends ProxyMethodHandler>> handlerCacheMap = new ConcurrentHashMap<>();

    @Override
    default Proxy.Result<?> onProxy(Object obj, Method method, Object[] args, MethodProxy proxy, ProxyContext context) throws Throwable {
        final CacheMapKey key = new CacheMapKey(method, getClass());
        final Proxy.Result<? extends ProxyMethodHandler> methodResult = handlerCacheMap.get(key);
        if (methodResult != null) {
            if (methodResult.isSuccess()) {
                return methodResult.getResult().onProxy(obj, method, args, proxy, context);
            } else {
                return Proxy.failed();
            }
        }
        final ProxyMethodHandler handler = getHandler(obj, method, args, proxy, context);
        if (handler == ProxyMethodHandler.failed) {
            handlerCacheMap.put(key, Proxy.failed());
        } else {
            handlerCacheMap.put(key, Proxy.alloc(handler));
        }
        return handler.onProxy(obj, method, args, proxy, context);
    }

    @SuppressWarnings("unused")
    @NotNull
    default ProxyMethodHandler getHandler(Object obj, Method method, Object[] args, MethodProxy proxy, ProxyContext context) throws Throwable {
        final Method selfMethod;
        try {
            String methodName = context.get(ProxyContext.methodNameKey);
            if (StringUtils.isEmpty(methodName)) {
                methodName = method.getName();
            }

            selfMethod = getClass().getMethod(methodName, method.getParameterTypes());
            selfMethod.setAccessible(true);
            return (obj1, method1, args1, proxy1, methodName1) -> Proxy.of(selfMethod.invoke(this, args1));
        } catch (Exception e) {
            return ProxyMethodHandler.failed;
        }
    }

    @Data
    final class CacheMapKey {
        @NotNull
        private Method method;
        @NotNull
        private Class<? extends ProxyMethod> clazz;

        // hash cache
        private int hash;

        public CacheMapKey(@NotNull Method method, @NotNull Class<? extends ProxyMethod> clazz) {
            this.method = method;
            this.clazz = clazz;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CacheMapKey)) return false;
            CacheMapKey that = (CacheMapKey) o;
            return clazz == that.clazz && (method == that.method || Objects.equals(method, that.method));
        }

        @Override
        public int hashCode() {
            if (hash != 0) {
                hash = Objects.hash(method, clazz);
            }
            return hash;
        }
    }
}