package com.lcy0x1.base.proxy.container;

import com.lcy0x1.base.proxy.handler.ProxyMethod;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface MutableProxyMethodContainer<T extends ProxyMethod> extends ProxyMethodContainer<T> {
    int addProxy(@Nullable T proxy);

    boolean addAllProxy(Collection<T> proxy);

    boolean removeProxy(T proxy);

    T removeProxy(int index);
}
