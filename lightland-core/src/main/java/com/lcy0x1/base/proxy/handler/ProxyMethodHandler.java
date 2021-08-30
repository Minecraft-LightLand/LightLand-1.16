package com.lcy0x1.base.proxy.handler;

import com.lcy0x1.base.proxy.Proxy;
import com.lcy0x1.base.proxy.ProxyContext;
import com.lcy0x1.base.proxy.Result;
import net.sf.cglib.proxy.MethodProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public interface ProxyMethodHandler {
    ProxyMethodHandler failed = (proxyMethod, obj, method, args, proxy, methodName) -> Result.failed();

    @Nullable
    Result<?> onProxy(ProxyMethod proxyMethod, @NotNull Proxy<?> obj, @NotNull Method method, @NotNull Object[] args,
                      @NotNull MethodProxy proxy, @NotNull ProxyContext context) throws Throwable;
}
