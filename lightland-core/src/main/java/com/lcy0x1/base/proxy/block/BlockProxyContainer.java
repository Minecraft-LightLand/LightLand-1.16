package com.lcy0x1.base.proxy.block;

import com.lcy0x1.base.proxy.block.annotation.ForEachProxy;
import com.lcy0x1.base.proxy.block.annotation.ForFirstProxy;
import net.minecraft.block.Block;
import net.minecraft.state.StateContainer;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Predicate;

public interface BlockProxyContainer<T extends Proxy> {
    Predicate<Object> stateContainerBuilderFilter = o -> o instanceof StateContainer.Builder;
    String[] errMsgSearchList = {"%M", "%B", "%A"};

    @NotNull
    BlockProxy<T> getProxy();

    /**
     * will be call when proxy method invoke.
     * 在代理方法被调用时，该方法会被调用
     * 大体流程是
     */
    default BlockProxy.Result<?> onProxy(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        HandlerCache.OnProxy handler = HandlerCache.INSTANCE.getHandler(method);
        if (handler != null) {
            return handler.onProxy(obj, method, args, proxy);
        }

        for (Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof ForEachProxy) {
                ForEachProxy forEachProxy = (ForEachProxy) annotation;
                handler = onForeachProxy(obj, method, args, proxy, forEachProxy);
                break;
            } else if (annotation instanceof ForFirstProxy) {
                final ForFirstProxy forFirstProxy = (ForFirstProxy) annotation;
                handler = onForFirstProxy(obj, method, args, proxy, forFirstProxy);
                break;
            }
        }

        if (handler == null) {
            handler = HandlerCache.callSuper;
        }
        HandlerCache.INSTANCE.setHandler(method, handler);

        return handler.onProxy(obj, method, args, proxy);
    }

    default HandlerCache.OnProxy onForFirstProxy(Object obj, Method method, Object[] args, MethodProxy proxy, ForFirstProxy forFirstProxy) throws Throwable {
        final Collection<Class<?>> classes;
        switch (forFirstProxy.type().length) {
            case 0:
                return HandlerCache.callSuper;
            case 1:
                classes = Collections.singletonList(forFirstProxy.type()[0]);
                break;
            default:
                classes = new HashSet<>(Arrays.asList(forFirstProxy.type()));
        }
        return (o, m, a, p) -> onForFirstProxy(o, m, a, p, forFirstProxy, classes);
    }

    default BlockProxy.Result<?> onForFirstProxy(Object obj, Method method, Object[] args, MethodProxy proxy, ForFirstProxy forFirstProxy, Collection<Class<?>> classes) throws Throwable {
        if (!(obj instanceof BlockProxyContainer<?>)) return BlockProxy.Result.failed;
        final BlockProxyContainer<?> block = (BlockProxyContainer<?>) obj;

        final BlockProxy.Result<?> result = block.getProxy().forFirstProxy(p -> {
            if (classes.contains(p.getClass()) || Arrays.stream(p.getClass().getInterfaces()).anyMatch(classes::contains)) {
                return p.onProxy(obj, method, args, proxy);
            } else {
                return BlockProxy.failed();
            }
        });

        if (result != null && result.success) {
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
            // todo use efficient contains
            if (errMsg.contains(errMsgSearchList[0])) {
                replacementList[0] = method.toString();
            }
            if (errMsg.contains(errMsgSearchList[1])) {
                replacementList[1] = block.toString();
            }
            if (errMsg.contains(errMsgSearchList[2])) {
                replacementList[2] = Arrays.toString(args);
            }

            errMsg = StringUtils.replaceEach(errMsg, errMsgSearchList, replacementList);
            throw forFirstProxy.errClass().getConstructor(String.class).newInstance(errMsg);
        }
        return BlockProxy.Result.failed;
    }

    default HandlerCache.OnProxy onForeachProxy(Object obj, Method method, Object[] args, MethodProxy proxy, ForEachProxy forEachProxy) {
        if (!(obj instanceof BlockProxyContainer<?>)) return HandlerCache.callSuper;
        Class<?>[] type = forEachProxy.type();
        Collection<Class<?>> classes;
        switch (type.length) {
            case 0:
                return HandlerCache.callSuper;
            case 1:
                classes = Collections.singletonList(type[0]);
                break;
            default:
                classes = new HashSet<>(Arrays.asList(type));
        }
        return onForeachProxy((BlockProxyContainer<?>) obj, method, args, proxy, forEachProxy, classes);
    }

    @Nullable
    default HandlerCache.OnProxy onForeachProxy(BlockProxyContainer<?> block, Method method, Object[] args, MethodProxy proxy, ForEachProxy forEachProxy, Collection<Class<?>> classes) {
        return (o, m, a, proxy1) -> {
            if (!(o instanceof Block)) return BlockProxy.Result.failed;
            getProxy().forEachProxy(p -> {
                if (classes.contains(p.getClass()) || Arrays.stream(p.getClass().getInterfaces()).anyMatch(classes::contains)) {
                    p.onProxy(o, m, a, proxy1);
                }
            });
            return BlockProxy.of(null);
        };
    }
}
