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
    public <R> Result<R> forFirstProxy(Function<T, Result<R>> action) {
        for (T t : proxyList) {
            Result<R> result = action.apply(t);
            if (result != null && result.success) {
                return result;
            }
        }
        return BlockProxy.failed();
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
