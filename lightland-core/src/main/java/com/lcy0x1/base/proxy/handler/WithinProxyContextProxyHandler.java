package com.lcy0x1.base.proxy.handler;

import com.lcy0x1.base.proxy.Proxy;
import com.lcy0x1.base.proxy.ProxyContext;
import com.lcy0x1.base.proxy.Result;
import com.lcy0x1.base.proxy.container.WithinProxyContextConfig;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class WithinProxyContextProxyHandler implements ProxyHandler {
    final ProxyHandler handler;
    final WithinProxyContextConfig withinProxyContext;

    public WithinProxyContextProxyHandler(ProxyHandler handler, WithinProxyContextConfig withinProxyContext) {
        this.handler = handler;
        this.withinProxyContext = withinProxyContext;
    }

    @Override
    public Result<?> onProxy(Proxy<?> obj, Method method, Object[] args, MethodProxy proxy, ProxyContext context) throws Throwable {
        return ProxyContext.withThreadLocalProxyContext(context,
            () -> {
                if (withinProxyContext.isProxy() || withinProxyContext.isBlock()) {
                    context.put(ProxyContext.proxy, obj);
                }
                return handler.onProxy(obj, method, args, proxy, context);
            });
    }
}
