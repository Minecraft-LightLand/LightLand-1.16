package com.lcy0x1.base.proxy.block;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor
public class ListBlockProxy<T> implements MutableBlockProxy<T> {
    @NotNull
    private final List<T> proxyList;

    public ListBlockProxy() {
        this(new ArrayList<>());
    }

    @Override
    public void forEachProxy(Consumer<T> action) {
        for (T t : proxyList) {
            action.accept(t);
        }
    }

    @Override
    public boolean forFirstProxy(Function<T, Boolean> action) {
        for (T t : proxyList) {
            if (action.apply(t)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int addProxy(T proxy) {
        proxyList.add(proxy);
        return proxyList.size() - 1;
    }

    @Override
    public boolean addAllProxy(Collection<T> proxy) {
        return proxyList.addAll(proxy);
    }

    @Override
    public void removeProxy(T proxy) {
        proxyList.remove(proxy);
    }

    @Override
    public void removeProxy(int index) {
        proxyList.remove(index);
    }
}
