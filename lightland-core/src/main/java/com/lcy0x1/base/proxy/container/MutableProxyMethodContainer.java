package com.lcy0x1.base.proxy.container;

import com.lcy0x1.base.proxy.handler.ProxyMethod;

import java.util.Collection;

public interface MutableProxyMethodContainer<T extends ProxyMethod> extends ProxyMethodContainer<T> {
    int addProxy(T proxy);

    boolean addAllProxy(Collection<T> proxy);

    boolean removeProxy(T proxy);

    T removeProxy(int index);
}
