package com.lcy0x1.base.proxy;

import com.lcy0x1.base.proxy.annotation.ForEachProxy;
import com.lcy0x1.base.proxy.annotation.ForFirstProxy;
import com.lcy0x1.base.proxy.container.ProxyMethodContainer;
import com.lcy0x1.base.proxy.handler.*;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public interface Proxy<T extends ProxyMethod> {
    String[] errMsgSearchList = {"%M", "%B", "%A"};

    @NotNull
    ProxyMethodContainer<? extends T> getProxyContainer() throws Throwable;

    /**
     * will be call when proxy method invoke.
     * 在代理方法被调用时，该方法会被调用
     */
    default Result<?> onProxy(Method method, Object[] args, MethodProxy proxy) throws Throwable {
        OnProxy handler = ProxyHandlerCache.INSTANCE.getHandler(method);
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
    default OnProxy getHandler(Method method, Object[] args, MethodProxy proxy) {
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
    static OnProxy onForeachProxy(Method method, ForEachProxy forEachProxy) {
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

        return new OnForeachProxyHandler(proxyContext, forEachProxy);
    }

    static OnProxy onForFirstProxy(Method method, ForFirstProxy forFirstProxy) {
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

        return new OnForFirstProxyHandler(forFirstProxy, classes, proxyContext);
    }
}
