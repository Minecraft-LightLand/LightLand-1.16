package com.lcy0x1.base.proxy.block;

import java.util.Collection;

public interface MutableBlockProxy<T> extends BlockProxy<T> {
    int addProxy(T proxy);

    boolean addAllProxy(Collection<T> proxy);

    void removeProxy(T proxy);

    void removeProxy(int index);
}
