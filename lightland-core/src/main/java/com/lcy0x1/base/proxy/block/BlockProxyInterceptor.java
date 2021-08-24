package com.lcy0x1.base.proxy.block;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class BlockProxyInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (obj instanceof BlockProxyContainer<?>) {
            BlockProxyContainer<?> blockProxy = (BlockProxyContainer<?>) obj;
            return blockProxy.onProxy(obj, method, args, proxy);
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }
}