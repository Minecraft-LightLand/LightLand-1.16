package com.lcy0x1.base.proxy.container;

import com.lcy0x1.base.proxy.Reflections;
import com.lcy0x1.base.proxy.handler.ForEachProxyHandler;
import com.lcy0x1.base.proxy.handler.ProxyHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ListProxyHandler<T extends ProxyHandler> implements Iterable<T> {
    @NotNull
    @Getter
    protected final ArrayList<T> proxyList = new ArrayList<>();
    protected Object[] elementData = null;
    @Getter
    protected volatile long lastModify = System.nanoTime();

    public int size() {
        return proxyList.size();
    }

    public boolean isEmpty() {
        return proxyList.isEmpty();
    }

    protected synchronized void modified() {
        lastModify = System.nanoTime();
        try {
            elementData = Reflections.getElementData(proxyList);
        } catch (Exception ignored) {
        }
    }

    public synchronized int addProxy(T proxy) {
        lastModify = System.nanoTime();
        proxyList.add(proxy);
        modified();
        return proxyList.size() - 1;
    }

    public synchronized boolean addAllProxy(Collection<T> proxy) {
        lastModify = System.nanoTime();
        final boolean addAll = proxyList.addAll(proxy);
        modified();
        return addAll;
    }

    public synchronized boolean removeProxy(T proxy) {
        lastModify = System.nanoTime();
        final boolean remove = proxyList.remove(proxy);
        modified();
        return remove;
    }

    public synchronized T removeProxy(int index) {
        lastModify = System.nanoTime();
        final T remove = proxyList.remove(index);
        modified();
        return remove;
    }

    public void clear() {
        proxyList.clear();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return proxyList.iterator();
    }

    @SuppressWarnings("unchecked")
    public void forEachProxy(ForEachProxyHandler<T> action) throws Throwable {
        final Object[] elementData = this.elementData;
        if (elementData != null) {
            final int size = proxyList.size();
            for (int i = 0; i < size; i++) {
                action.accept((T) elementData[i]);
            }
        } else {
            for (T t : this) {
                action.accept(t);
            }
        }
    }
}
