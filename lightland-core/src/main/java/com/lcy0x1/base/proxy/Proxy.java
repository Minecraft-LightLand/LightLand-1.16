package com.lcy0x1.base.proxy;

import com.lcy0x1.base.proxy.annotation.ForEachProxy;
import com.lcy0x1.base.proxy.annotation.ForFirstProxy;
import com.lcy0x1.base.proxy.container.ProxyContainer;
import com.lcy0x1.base.proxy.handler.*;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public interface Proxy<T extends ProxyMethod> {
    Logger log = LogManager.getLogger(Proxy.class);

    @NotNull
    ProxyContainer<? extends T> getProxyContainer() throws Throwable;

    /**
     * will be call when proxy method invoke.
     * 在代理方法被调用时，该方法会被调用
     */
    default Result<?> onProxy(Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.getName().equals("hasTileEntity")) {
            log.info("hasTileEntity");
        }
        ProxyOnProxyHandler handler = ProxyHandlerCache.INSTANCE.getHandler(method);
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
    default ProxyOnProxyHandler getHandler(Method method, Object[] args, MethodProxy proxy) throws Throwable {
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof ForEachProxy) {
                ForEachProxy forEachProxy = (ForEachProxy) annotation;
                return onForeachProxy(method, forEachProxy, args, proxy);
            } else if (annotation instanceof ForFirstProxy) {
                final ForFirstProxy forFirstProxy = (ForFirstProxy) annotation;
                return onForFirstProxy(method, forFirstProxy);
            }
        }
        return null;
    }

    @NotNull
    default ProxyOnProxyHandler onForeachProxy(Method method, ForEachProxy forEachProxy, Object[] args, MethodProxy proxy) throws Throwable {
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

        //log.info("proxy method: {}, name: {}", method, methodName);
        //if (methodName.equals("createBlockStateDefinition")) {
        //    log.info("createBlockStateDefinition handler list: {}", CollectionsKt.toList(getProxyContainer()));
        //    proxyContext.put(ProxyContext.loggerKey, LogManager.getLogger("com.lcy0x1.base.proxy.log"));
        //}
        return new ProxyOnForeachProxyHandlerHandler(proxyContext, forEachProxy);
    }

    static ProxyOnProxyHandler onForFirstProxy(Method method, ForFirstProxy forFirstProxy) {
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

        return new ProxyOnForFirstProxyHandlerHandler(forFirstProxy, classes, proxyContext);
    }
}
