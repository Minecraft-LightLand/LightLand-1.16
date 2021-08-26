package com.lcy0x1.base.proxy;

import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.Data;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public interface ProxyMethod extends ProxyHandler {
    ProxyMethod failed = new ProxyMethod() {
        @Override
        public Result<?> onProxy(Object obj, Method method, Object[] args, MethodProxy proxy, ProxyContext context) throws Throwable {
            return Result.failed();
        }
    };
    Map<CacheMapKey, Result<? extends ProxyHandler>> handlerCacheMap = new ConcurrentHashMap<>();

    @Override
    default Result<? extends Object> onProxy(Object obj, Method method, Object[] args, MethodProxy proxy, ProxyContext context) throws Throwable {
        final CacheMapKey key = new CacheMapKey(method, getClass());
        final Result<? extends ProxyHandler> methodResult = handlerCacheMap.get(key);
        if (methodResult != null) {
            if (methodResult.isSuccess()) {
                return methodResult.getResult().onProxy(obj, method, args, proxy, context);
            } else {
                return Result.failed();
            }
        }
        final ProxyHandler handler = getHandler(obj, method, args, proxy, context);
        if (handler == ProxyHandler.failed) {
            handlerCacheMap.put(key, Result.failed());
        } else {
            handlerCacheMap.put(key, Result.alloc(handler));
        }
        return handler.onProxy(obj, method, args, proxy, context);
    }

    @SuppressWarnings("unused")
    @NotNull
    default ProxyHandler getHandler(Object obj, Method method, Object[] args, MethodProxy proxy, ProxyContext context) throws Throwable {
        String methodName = context.get(ProxyContext.methodNameKey);
        if (StringUtils.isEmpty(methodName)) {
            methodName = method.getName();
        }

        // get method by ReflectASM
        try {
            final MethodAccess methodAccess = Reflections.getMethodAccess(getClass());
            final int index = methodAccess.getIndex(methodName, method.getParameterTypes());
            return (obj1, method1, args1, proxy1, context1) -> Result.of(methodAccess.invoke(this, index, args1));
        } catch (Exception ignored) {
        }

        // get method by java reflect
        try {
            final Method declaredMethod = getClass().getDeclaredMethod(methodName, method.getParameterTypes());
            declaredMethod.setAccessible(true);
            return (obj1, method1, args1, proxy1, context1) -> Result.of(declaredMethod.invoke(this, args1));
        } catch (Exception ignored) {
        }

        return ProxyHandler.failed;
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