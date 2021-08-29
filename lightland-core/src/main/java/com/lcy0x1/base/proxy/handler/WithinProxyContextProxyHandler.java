package com.lcy0x1.base.proxy.handler;

import com.lcy0x1.base.proxy.Proxy;
import com.lcy0x1.base.proxy.ProxyContext;
import com.lcy0x1.base.proxy.Result;
import com.lcy0x1.base.proxy.container.WithinProxyContextConfig;
import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class WithinProxyContextProxyHandler implements ProxyHandler {
    final ProxyHandler handler;
    final WithinProxyContextConfig withinProxyContext;

    public WithinProxyContextProxyHandler(ProxyHandler handler, WithinProxyContextConfig withinProxyContext) {
        this.handler = handler;
        this.withinProxyContext = withinProxyContext;
    }

    @Override
    public Result<?> onProxy(ProxyMethod proxyMethod, @NotNull Proxy<?> obj, @NotNull Method method, @NotNull Object[] args, @NotNull MethodProxy proxy, @NotNull ProxyContext context) throws Throwable {
        return ProxyContext.withThreadLocalProxyContext(context,
                () -> {
                    if (withinProxyContext.isProxy() || withinProxyContext.isBlock()) {
                        context.put(ProxyContext.proxy, obj);
                    }
                    if (withinProxyContext.isPreSuper() && context.get(ProxyContext.pre) == null) {
                        context.putResult(ProxyContext.objectPre, proxy.invokeSuper(obj, args));
                    }
                    final Result<?> result = handler.onProxy(proxyMethod, obj, method, args, proxy, context);
                    if (result != null && result.isSuccess() && withinProxyContext.isPre()) {
                        context.put(ProxyContext.pre, result.snapshot());
                    }
                    return result;
                });
    }
}
