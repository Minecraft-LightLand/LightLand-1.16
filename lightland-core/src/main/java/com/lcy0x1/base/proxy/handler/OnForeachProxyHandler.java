package com.lcy0x1.base.proxy.handler;

import com.lcy0x1.base.proxy.Proxy;
import com.lcy0x1.base.proxy.ProxyContext;
import com.lcy0x1.base.proxy.Reflections;
import com.lcy0x1.base.proxy.Result;
import com.lcy0x1.base.proxy.annotation.ForEachProxy;
import com.lcy0x1.base.proxy.container.ListProxyHandler;
import com.lcy0x1.base.proxy.container.ProxyMethodContainer;
import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class OnForeachProxyHandler implements OnProxy {
    private final ProxyContext context;
    private final ForEachProxy forEachProxy;
    private volatile ListProxyHandler<ProxyHandler> proxyMethods = null;
    private volatile long lastModify = 0;
    // cache forEachProxy.keepContext()
    private final boolean keepContext;

    public OnForeachProxyHandler(ProxyContext context, ForEachProxy forEachProxy) {
        this.context = context;
        this.forEachProxy = forEachProxy;
        keepContext = forEachProxy.keepContext();
    }

    @Override
    public Result<?> onProxy(Proxy<?> o, Method m, Object[] a, MethodProxy p) throws Throwable {
        final ProxyContext subContext = context.getSubContext();
        final ProxyMethodContainer<?> proxyContainer = o.getProxyContainer();

        Result<?> result = noNeedUseCache(proxyContainer, subContext, o, m, a, p);
        if (result != null) {
            return result;
        }
        result = useCache(proxyContainer, subContext, o, m, a, p);
        if (result != null) {
            return result;
        }
        return rebuildCache(proxyContainer, subContext, o, m, a, p);
    }

    private Result<?> noNeedUseCache(
            @NotNull ProxyMethodContainer<?> proxyContainer, ProxyContext c,
            Proxy<?> o, Method m, Object[] a, MethodProxy p
    ) throws Throwable {
        if (proxyContainer.size() < 16) {
            return loop(proxyContainer, c, o, m, a, p);
        }
        return null;
    }

    private Result<?> useCache(
            @NotNull ProxyMethodContainer<?> proxyContainer, ProxyContext c,
            Proxy<?> o, Method m, Object[] a, MethodProxy p
    ) throws Throwable {
        if (lastModify == proxyContainer.getLastModify() && proxyMethods != null) {
            return loop(proxyMethods, c, o, m, a, p);
        }
        return null;
    }

    private Result<?> rebuildCache(
            @NotNull ProxyMethodContainer<?> proxyContainer, ProxyContext c,
            Proxy<?> o, Method m, Object[] a, MethodProxy p
    ) throws Throwable {
        System.out.println("rebuild loop cache");
        final Reflections.Reference<Result<?>> r = new Reflections.Reference<>(Result.of().snapshot());
        if (forEachProxy.type() == ForEachProxy.LoopType.AFTER) {
            r.setValue(Result.of(p.invokeSuper(o, a)).snapshot());
        }
        final long lastModify = proxyContainer.getLastModify();
        final ListProxyHandler<ProxyHandler> proxyMethods = new ListProxyHandler<>();
        proxyContainer.forEachProxy(proxyMethod -> {
            final Result<?> result = call(proxyMethod, o, m, a, p, c);
            if (result != null && result.isSuccess()) {
                proxyMethods.addProxy(proxyMethod);
                r.setValue(result.snapshot());
            }
        });
        this.proxyMethods = proxyMethods;
        this.lastModify = lastModify;

        if (forEachProxy.type() == ForEachProxy.LoopType.BEFORE_WITH_RETURN) {
            return r.getValue();
        }
        return Result.failed();
    }

    private Result<?> call(
            @NotNull ProxyHandler proxyMethod,
            Proxy<?> o, Method m, Object[] a, MethodProxy p, ProxyContext c
    ) throws Throwable {
        final Result<?> result = proxyMethod.onProxy(o, m, a, p, c);
        if (!keepContext) {
            c.clean();
        }
        return result;
    }

    private Result<?> loop(
            @NotNull ProxyMethodContainer<? extends ProxyHandler> proxyMethods, ProxyContext c,
            Proxy<?> o, Method m, Object[] a, MethodProxy p
    ) throws Throwable {
        if (forEachProxy.type() == ForEachProxy.LoopType.BEFORE_WITH_RETURN) {
            final Reflections.Reference<Result<?>> r = new Reflections.Reference<>(Result.failed());
            proxyMethods.forEachProxy(proxyMethod -> {
                final Result<?> result = call(proxyMethod, o, m, a, p, c);
                if (result != null && result.isSuccess()) {
                    r.setValue(result.snapshot());
                }
            });
            return r.getValue();

        } else {
            Result<?> result = Result.failed();
            if (forEachProxy.type() == ForEachProxy.LoopType.AFTER) {
                result = Result.of(p.invokeSuper(o, a)).snapshot();
            }
            proxyMethods.forEachProxy(proxyMethod -> {
                call(proxyMethod, o, m, a, p, c);
            });

            return result;
        }
    }
}
