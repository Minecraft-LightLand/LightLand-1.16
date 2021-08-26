package com.lcy0x1.base.proxy;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public interface ProxyHandler {
    ProxyHandler failed = (obj, method, args, proxy, methodName) -> Result.failed();

    Result<?> onProxy(Object obj, Method method, Object[] args, MethodProxy proxy, ProxyContext context) throws Throwable;
}
