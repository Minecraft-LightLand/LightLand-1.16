package com.lcy0x1.base.proxy;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@AllArgsConstructor
@Log4j2
public class ListProxy<T extends ProxyMethod> implements MutableProxy<T> {
    @NotNull
    private final List<T> proxyList;

    public ListProxy() {
        this(new ArrayList<>());
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

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return proxyList.iterator();
    }
}
