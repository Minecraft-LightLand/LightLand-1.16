package com.lcy0x1.base.proxy.handler;

import com.hikarishima.lightland.util.LightLandStringUtils;
import com.lcy0x1.base.proxy.Proxy;
import com.lcy0x1.base.proxy.ProxyContext;
import com.lcy0x1.base.proxy.Result;
import com.lcy0x1.base.proxy.annotation.ForFirstProxy;
import com.lcy0x1.base.proxy.container.ProxyMethodContainer;
import lombok.extern.log4j.Log4j2;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

@Log4j2
public class OnForFirstProxyHandler implements OnProxy {
    private static final String[] errMsgSearchList = {"%M", "%B", "%A"};
    final ForFirstProxy forFirstProxy;
    final Collection<Class<?>> classes;
    volatile long lastModify = 0;
    final ProxyContext context;

    public OnForFirstProxyHandler(ForFirstProxy forFirstProxy, Collection<Class<?>> classes, ProxyContext context) {
        this.forFirstProxy = forFirstProxy;
        this.classes = classes;
        this.context = context;
    }

    @Override
    public Result<?> onProxy(Proxy<?> p, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        final ProxyMethodContainer<?> proxyContainer = p.getProxyContainer();
        final Result<ProxyHandler> proxyMethod = context.get(ProxyContext.proxyMethod);
        final ProxyContext context = this.context.getSubContext();
        Result<?> result = null;

        context.put(ProxyContext.proxy, p);

        if (proxyMethod == null || lastModify != proxyContainer.getLastModify()) {
            final long lastModify = proxyContainer.getLastModify();
            result = loop(proxyContainer, context, p, method, args, proxy);
            this.lastModify = lastModify;
        } else if (proxyMethod.isSuccess()) {
            // use ProxyMethod cache
            result = proxyMethod.getResult().onProxy(p, method, args, proxy, context);
        }

        if (result != null && result.isSuccess()) {
            return result;
        }

        // when request not handled
        if (forFirstProxy.must()) {
            // generate error message
            final String errMsg = buildErrorMsg(method, args, p);
            throw forFirstProxy.errClass().getConstructor(String.class).newInstance(errMsg);
        }
        return Result.failed();
    }

    public Result<?> loop(ProxyMethodContainer<?> proxyContainer, ProxyContext context,
                          Proxy<?> obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return proxyContainer.forFirstProxy(p -> {
            if (classes != null && classes.stream().noneMatch(c -> c.isInstance(p))) {
                return Result.failed();
            }
            final Result<?> methodResult = p.onProxy(obj, method, args, proxy, context);
            // check cache config
            if (forFirstProxy.cache() && !Boolean.FALSE.equals(context.getAndRemove(ProxyContext.cacheFirstProxyMethod))) {
                // if get cache command
                if (methodResult != null && methodResult.isSuccess()) {
                    this.context.put(ProxyContext.proxyMethod, Result.alloc(p));
                } else {
                    this.context.put(ProxyContext.proxyMethod, Result.failed());
                }
            }
            if (Boolean.TRUE.equals(context.getAndRemove(ProxyContext.continueFirstProxyMethod))) {
                return Result.failed();
            } else {
                return methodResult;
            }
        });
    }

    public String buildErrorMsg(Method method, Object[] args, Proxy<?> container) {
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
        return StringUtils.replaceEach(errMsg, errMsgSearchList, replacementList);
    }
}
