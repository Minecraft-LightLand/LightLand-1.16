package com.lcy0x1.base.proxy;

import java.util.Collection;

public interface MutableProxy<T extends ProxyMethod> extends Proxy<T> {
    int addProxy(T proxy);

    boolean addAllProxy(Collection<T> proxy);

    void removeProxy(T proxy);

    void removeProxy(int index);
}
