package com.lcy0x1.base.proxy.handler;

import com.lcy0x1.base.proxy.Proxy;
import com.lcy0x1.base.proxy.ProxyContext;
import com.lcy0x1.base.proxy.Result;
import com.lcy0x1.base.proxy.annotation.WithinProxyContext;
import net.minecraft.block.Block;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class WithinProxyContextProxyHandler implements ProxyHandler {
    final ProxyHandler handler;
    final WithinProxyContext withinProxyContext;

    public WithinProxyContextProxyHandler(ProxyHandler handler, WithinProxyContext withinProxyContext) {
        this.handler = handler;
        this.withinProxyContext = withinProxyContext;
    }

    @Override
    public Result<?> onProxy(Proxy<?> obj, Method method, Object[] args, MethodProxy proxy, ProxyContext context) throws Throwable {
        return ProxyContext.withThreadLocalProxyContext(context,
            () -> {
                if (withinProxyContext.block() && obj instanceof Block) {
                    context.put(ProxyContext.block, (Block) obj);
                }
                if (withinProxyContext.proxy()) {
                    context.put(ProxyContext.proxy, obj);
                }
                return handler.onProxy(obj, method, args, proxy, context);
            });
    }
}
