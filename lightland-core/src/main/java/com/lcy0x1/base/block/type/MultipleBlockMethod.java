package com.lcy0x1.base.block.type;

import com.lcy0x1.base.proxy.Proxy;
import com.lcy0x1.base.proxy.ProxyContext;
import com.lcy0x1.base.proxy.Result;
import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public interface MultipleBlockMethod extends BlockMethod {
    @Override
    default Result<?> onProxy(@NotNull Proxy<?> obj, @NotNull Method method, @NotNull Object[] args, @NotNull MethodProxy proxy, @NotNull ProxyContext context) throws Throwable {
        context.put(ProxyContext.cacheFirstProxyMethod, false);
        return BlockMethod.super.onProxy(obj, method, args, proxy, context);
    }
}
