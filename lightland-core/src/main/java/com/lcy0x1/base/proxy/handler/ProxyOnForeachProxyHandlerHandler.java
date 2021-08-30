package com.lcy0x1.base.proxy.handler;

import com.lcy0x1.base.proxy.Proxy;
import com.lcy0x1.base.proxy.ProxyContext;
import com.lcy0x1.base.proxy.Reflections;
import com.lcy0x1.base.proxy.Result;
import com.lcy0x1.base.proxy.annotation.ForEachProxy;
import com.lcy0x1.base.proxy.container.ListProxyHandler;
import com.lcy0x1.base.proxy.container.ProxyContainer;
import lombok.extern.log4j.Log4j2;
import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

@Log4j2
public class ProxyOnForeachProxyHandlerHandler implements ProxyOnProxyHandler {
    private final ProxyContext context;
    private final ForEachProxy forEachProxy;
    private volatile ListProxyHandler<ProxyMethod> proxyMethods = null;
    private volatile long lastModify = 0;
    // cache forEachProxy.keepContext()
    private final boolean keepContext;

    public ProxyOnForeachProxyHandlerHandler(ProxyContext context, ForEachProxy forEachProxy) {
        this.context = context;
        this.forEachProxy = forEachProxy;
        keepContext = forEachProxy.keepContext();
    }

    @Override
    public Result<?> onProxy(Proxy<?> o, Method m, Object[] a, MethodProxy p) throws Throwable {
        final ProxyContext subContext = context.getSubContext();
        final ProxyContainer<?> proxyContainer = o.getProxyContainer();

        //log.info("onProxy:\n\tForEachProxy: {}\n\tproxy: {},\n\tmethod: {},\n\targs: {},\n\tmethodProxy: {}\n\tcontext: {}",
        //    forEachProxy, o, m, a, p, context);
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
            @NotNull ProxyContainer<?> proxyContainer, ProxyContext c,
            Proxy<?> o, Method m, Object[] a, MethodProxy p
    ) throws Throwable {
        if (proxyContainer.isEmpty()) {
            return Result.failed();
        }
        if (proxyContainer.size() < 16) {
            return loop(proxyContainer, c, o, m, a, p);
        }
        return null;
    }

    private Result<?> useCache(
            @NotNull ProxyContainer<?> proxyContainer, ProxyContext c,
            Proxy<?> o, Method m, Object[] a, MethodProxy p
    ) throws Throwable {
        if (lastModify == proxyContainer.getLastModify() && proxyMethods != null) {
            if (proxyMethods.isEmpty()) {
                return Result.failed();
            }
            return loop(proxyMethods, c, o, m, a, p);
        }
        return null;
    }

    private Result<?> rebuildCache(
            @NotNull ProxyContainer<?> proxyContainer, ProxyContext c,
            Proxy<?> o, Method m, Object[] a, MethodProxy p
    ) throws Throwable {
        final Reflections.Reference<Result<?>> r = new Reflections.Reference<>(Result.of());
        if (forEachProxy.type() == ForEachProxy.LoopType.AFTER) {
            Object result = p.invokeSuper(o, a);
            if (result instanceof Result<?>) {
                result = ((Result<?>) result).snapshot();
            }
            r.setValue(Result.of(result));
        }
        final long lastModify = proxyContainer.getLastModify();
        final ListProxyHandler<ProxyMethod> proxyMethods = new ListProxyHandler<>();
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

    @Nullable
    private Result<?> call(
            @NotNull ProxyMethod proxyMethod,
            Proxy<?> o, Method m, Object[] a, MethodProxy p, ProxyContext c
    ) throws Throwable {
        final Result<?> result = proxyMethod.onProxy(o, m, a, p, c);
        if (!keepContext) {
            c.clean();
        }
        return result;
    }

    private Result<?> loop(
            @NotNull ProxyContainer<? extends ProxyMethod> proxyMethods, ProxyContext c,
            Proxy<?> o, Method m, Object[] a, MethodProxy p
    ) throws Throwable {
        switch (forEachProxy.type()) {
            case BEFORE_WITH_RETURN:
            case AFTER:
                final Reflections.Reference<Result<?>> r = new Reflections.Reference<>(Result.failed());
                if (forEachProxy.type() == ForEachProxy.LoopType.AFTER) {
                    final Result<?> invokeSuper = Result.alloc(p.invokeSuper(o, a));
                    r.setValue(invokeSuper);
                    c.put(ProxyContext.pre, invokeSuper);
                }
                proxyMethods.forEachProxy(proxyMethod -> {
                    final Result<?> result = call(proxyMethod, o, m, a, p, c);
                    if (result != null && result.isSuccess()) {
                        r.setValue(result.snapshot());
                    }
                });
                return r.getValue();
            default:
                proxyMethods.forEachProxy(proxyMethod -> call(proxyMethod, o, m, a, p, c));
                return Result.failed();
        }
    }
}
