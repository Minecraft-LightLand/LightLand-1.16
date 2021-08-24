package com.lcy0x1.base.proxy.block;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;

public class BlockProxyInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (obj instanceof BlockProxy<?>) {
            BlockProxy<?> blockProxy = (BlockProxy<?>) obj;
            Arrays.stream(method.getAnnotations())
                .filter(annotation -> annotation instanceof ForEachProxy)
                .findFirst().ifPresent(annotation -> {
                    return;
                });
        }
        return proxy.invokeSuper(obj, args);
    }
}