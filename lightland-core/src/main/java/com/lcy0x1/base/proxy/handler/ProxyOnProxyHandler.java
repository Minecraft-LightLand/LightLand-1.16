package com.lcy0x1.base.proxy.handler;

import com.lcy0x1.base.proxy.Proxy;
import com.lcy0x1.base.proxy.Result;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public interface ProxyOnProxyHandler {
    Result<?> onProxy(Proxy<?> obj, Method method, Object[] args, MethodProxy proxy) throws Throwable;
}