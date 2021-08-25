package com.lcy0x1.base.proxy;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Log4j2
public class ListProxy<T extends ProxyMethod> implements MutableProxy<T> {
    @NotNull
    @Getter
    private final List<T> proxyList;
    @Getter
    private long lastModify = 0;

    public ListProxy() {
        this(new ArrayList<>());
    }

    public ListProxy(@NotNull List<T> proxyList) {
        this.proxyList = proxyList;
    }

    @Override
    public int addProxy(T proxy) {
        proxyList.add(proxy);
        return proxyList.size() - 1;
    }

    @Override
    public boolean addAllProxy(Collection<T> proxy) {
        lastModify = System.currentTimeMillis();
        return proxyList.addAll(proxy);
    }

    @Override
    public void removeProxy(T proxy) {
        lastModify = System.currentTimeMillis();
        proxyList.remove(proxy);
    }

    @Override
    public void removeProxy(int index) {
        lastModify = System.currentTimeMillis();
        proxyList.remove(index);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return proxyList.iterator();
    }
}
