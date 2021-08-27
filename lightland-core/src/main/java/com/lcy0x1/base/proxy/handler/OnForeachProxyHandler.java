package com.lcy0x1.base.proxy.handler;

import com.lcy0x1.base.proxy.Proxy;
import com.lcy0x1.base.proxy.ProxyContext;
import com.lcy0x1.base.proxy.Result;
import com.lcy0x1.base.proxy.container.ListProxyHandler;
import com.lcy0x1.base.proxy.container.ProxyMethodContainer;
import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class OnForeachProxyHandler implements OnProxy {
    private final ProxyContext context;
    private volatile ListProxyHandler<ProxyMethod> proxyMethods = null;
    private volatile long lastModify = 0;

    public OnForeachProxyHandler(ProxyContext context) {
        this.context = context;
    }

    @Override
    public Result<?> onProxy(Object o, Method m, Object[] a, MethodProxy p) throws Throwable {
        if (!(o instanceof Proxy<?>)) return Result.failed();
        final ProxyContext subContext = context.getSubContext();
        Proxy<?> proxy = (Proxy<?>) o;
        final ProxyMethodContainer<?> proxyContainer = proxy.getProxyContainer();

        if (noNeedUseCache(proxyContainer, subContext, o, m, a, p)) {
            return Result.failed();
        }
        if (useCache(proxyContainer, subContext, o, m, a, p)) {
            return Result.failed();
        }
        rebuildCache(proxyContainer, subContext, o, m, a, p);
        return Result.failed();
    }

    private boolean noNeedUseCache(
        @NotNull ProxyMethodContainer<?> proxyContainer, ProxyContext subContext,
        Object o, Method m, Object[] a, MethodProxy p
    ) throws Throwable {
        if (proxyContainer.size() < 16) {
            proxyContainer.forEachProxy(proxyMethod -> {
                proxyMethod.onProxy(o, m, a, p, subContext);
                subContext.clean();
            });
            return true;
        }
        return false;
    }

    private boolean useCache(
        @NotNull ProxyMethodContainer<?> proxyContainer, ProxyContext subContext,
        Object o, Method m, Object[] a, MethodProxy p
    ) throws Throwable {
        if (lastModify == proxyContainer.getLastModify() && proxyMethods != null) {
            proxyMethods.forEachProxy(proxyMethod -> {
                proxyMethod.onProxy(o, m, a, p, subContext);
                subContext.clean();
            });
            return true;
        }
        return false;
    }

    private void rebuildCache(
        @NotNull ProxyMethodContainer<?> proxyContainer, ProxyContext subContext,
        Object o, Method m, Object[] a, MethodProxy p
    ) throws Throwable {
        final long lastModify = proxyContainer.getLastModify();
        final ListProxyHandler<ProxyMethod> proxyMethods = new ListProxyHandler<>();
        proxyContainer.forEachProxy(proxyMethod -> {
            final Result<?> result = proxyMethod.onProxy(o, m, a, p, subContext);
            if (result != null && result.isSuccess()) {
                proxyMethods.addProxy(proxyMethod);
            }
            subContext.clean();
        });
        this.proxyMethods = proxyMethods;
        this.lastModify = lastModify;
    }
}
