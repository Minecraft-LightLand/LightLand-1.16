package com.lcy0x1.base.proxy;

import com.hikarishima.lightland.util.LightLandStringUtils;
import com.lcy0x1.base.proxy.annotation.ForEachProxy;
import com.lcy0x1.base.proxy.annotation.ForFirstProxy;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public interface Proxy<T extends ProxyMethod> {
    String[] errMsgSearchList = {"%M", "%B", "%A"};

    @NotNull
    ProxyMethodContainer<? extends T> getProxyContainer() throws Throwable;

    /**
     * will be call when proxy method invoke.
     * 在代理方法被调用时，该方法会被调用
     */
    default Result<?> onProxy(Method method, Object[] args, MethodProxy proxy) throws Throwable {
        ProxyHandlerCache.OnProxy handler = ProxyHandlerCache.INSTANCE.getHandler(method);
        if (handler != null) {
            return handler.onProxy(this, method, args, proxy);
        }

        handler = getHandler(method, args, proxy);
        if (handler == null) {
            handler = ProxyHandlerCache.callSuper;
        }
        ProxyHandlerCache.INSTANCE.setHandler(method, handler);

        return handler.onProxy(this, method, args, proxy);
    }

    @SuppressWarnings("unused")
    default ProxyHandlerCache.OnProxy getHandler(Method method, Object[] args, MethodProxy proxy) {
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof ForEachProxy) {
                ForEachProxy forEachProxy = (ForEachProxy) annotation;
                return onForeachProxy(method, forEachProxy);
            } else if (annotation instanceof ForFirstProxy) {
                final ForFirstProxy forFirstProxy = (ForFirstProxy) annotation;
                return onForFirstProxy(method, forFirstProxy);
            }
        }
        return null;
    }

    @NotNull
    static ProxyHandlerCache.OnProxy onForeachProxy(Method method, ForEachProxy forEachProxy) {
        final ProxyContext proxyContext = new ProxyContext();

        Class<?>[] type = forEachProxy.value();
        Collection<Class<?>> classes;
        switch (type.length) {
            case 0:
                classes = null;
                break;
            case 1:
                classes = Collections.singletonList(type[0]);
                break;
            default:
                classes = new HashSet<>(Arrays.asList(type));
        }
        if (classes != null) {
            proxyContext.put(ProxyContext.classes, classes);
        }

        String methodName = forEachProxy.name();
        if (StringUtils.isEmpty(methodName)) {
            methodName = method.getName();
        }
        proxyContext.put(ProxyContext.methodNameKey, methodName);

        return onForeachProxy(proxyContext);
    }

    @NotNull
    static ProxyHandlerCache.OnProxy onForeachProxy(ProxyContext context) {
        final AtomicReference<List<ProxyMethod>> proxyMethodListReference = new AtomicReference<>();
        final AtomicLong lastModify = new AtomicLong();
        return (o, m, a, proxy1) -> {
            if (!(o instanceof Proxy<?>)) return Result.failed();
            final ProxyContext subContext = context.getSubContext();
            Proxy<?> proxy = (Proxy<?>) o;
            final ProxyMethodContainer<?> proxyContainer = proxy.getProxyContainer();
            final List<ProxyMethod> proxyMethods = proxyMethodListReference.get();
            if (lastModify.get() == proxyContainer.getLastModify() && proxyMethods != null) {
                for (ProxyMethod p : proxyMethods) {
                    p.onProxy(o, m, a, proxy1, subContext);
                    subContext.clean();
                }
            } else {
                lastModify.set(proxyContainer.getLastModify());
                final List<ProxyMethod> newProxyMethods = new ArrayList<>();
                proxyContainer.forEachProxy(p -> {
                    final Result<?> result = p.onProxy(o, m, a, proxy1, subContext);
                    if (result != null && result.isSuccess()) {
                        newProxyMethods.add(p);
                    }
                    subContext.clean();
                });
                proxyMethodListReference.set(newProxyMethods);
            }
            return Result.failed();
        };
    }

    static ProxyHandlerCache.OnProxy onForFirstProxy(Method method, ForFirstProxy forFirstProxy) {
        final AtomicLong lastModify = new AtomicLong();
        final ProxyContext proxyContext = new ProxyContext();
        final Collection<Class<?>> classes;
        switch (forFirstProxy.value().length) {
            case 0:
                classes = null;
                break;
            case 1:
                classes = Collections.singletonList(forFirstProxy.value()[0]);
                break;
            default:
                classes = new HashSet<>(Arrays.asList(forFirstProxy.value()));
        }
        final String methodName;
        if (StringUtils.isEmpty(forFirstProxy.name())) {
            methodName = method.getName();
        } else {
            methodName = forFirstProxy.name();
        }
        proxyContext.put(ProxyContext.methodNameKey, methodName);

        return (o, m, a, p) -> onForFirstProxy(o, m, a, p, forFirstProxy, classes, lastModify, proxyContext.getSubContext());
    }

    static Result<?> onForFirstProxy(
        Object obj, Method method, Object[] args, MethodProxy proxy,
        ForFirstProxy forFirstProxy, Collection<Class<?>> classes, AtomicLong lastModify,
        ProxyContext context) throws Throwable {
        if (!(obj instanceof Proxy<?>)) return Result.failed();
        final Proxy<?> container = (Proxy<?>) obj;
        final ProxyMethodContainer<?> containerProxyMethodContainer = container.getProxyContainer();
        final Result<ProxyMethod> proxyMethod = context.get(ProxyContext.proxyMethod);
        Result<?> result = null;

        if (proxyMethod == null || lastModify.get() != containerProxyMethodContainer.getLastModify()) {
            lastModify.set(containerProxyMethodContainer.getLastModify());
            result = containerProxyMethodContainer.forFirstProxy(p -> {
                if (classes == null || classes.stream().anyMatch(c -> c.isInstance(p))) {
                    context.clean();
                    final Result<?> methodResult = p.onProxy(obj, method, args, proxy, context);
                    // check cache config
                    if (forFirstProxy.cache() && !Boolean.FALSE.equals(context.getAndRemove(ProxyContext.cacheFirstProxyMethod))) {
                        // if get cache command
                        if (methodResult != null && methodResult.isSuccess()) {
                            context.put(ProxyContext.proxyMethod, Result.alloc(p));
                        } else {
                            context.put(ProxyContext.proxyMethod, Result.failed());
                        }
                    }
                    return methodResult;
                }
                return Result.failed();

            });
        } else if (proxyMethod.isSuccess()) {
            // use ProxyMethod cache
            result = proxyMethod.getResult().onProxy(obj, method, args, proxy, context);
        }

        if (result != null && result.isSuccess()) {
            return result;
        }

        // when request not handled
        if (forFirstProxy.must()) {
            // generate error message
            String errMsg = forFirstProxy.errMsg();
            if (StringUtils.isBlank(errMsg)) {
                errMsg = "no proxy handled on method %M";
            }

            final String[] replacementList = new String[errMsgSearchList.length];
            final boolean[] contains = LightLandStringUtils.contains(errMsg, errMsgSearchList);
            if (contains[0]) {
                replacementList[0] = method.toString();
            }
            if (contains[1]) {
                replacementList[1] = container.toString();
            }
            if (contains[2]) {
                replacementList[2] = Arrays.toString(args);
            }

            errMsg = StringUtils.replaceEach(errMsg, errMsgSearchList, replacementList);
            throw forFirstProxy.errClass().getConstructor(String.class).newInstance(errMsg);
        }
        return Result.failed();
    }
}
