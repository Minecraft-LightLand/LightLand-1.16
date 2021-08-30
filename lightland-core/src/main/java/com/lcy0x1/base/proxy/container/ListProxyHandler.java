package com.lcy0x1.base.proxy.container;

import com.lcy0x1.base.proxy.Reflections;
import com.lcy0x1.base.proxy.Result;
import com.lcy0x1.base.proxy.handler.ForEachProxyHandler;
import com.lcy0x1.base.proxy.handler.ForFirstProxyHandler;
import com.lcy0x1.base.proxy.handler.ProxyMethod;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@Log4j2
public class ListProxyHandler<T extends ProxyMethod> implements ProxyContainer<T>, Iterable<T> {
    @NotNull
    @Getter
    protected final ArrayList<T> proxyList = new ArrayList<>();
    protected Object[] elementData = null;
    @Getter
    protected volatile long lastModify = System.nanoTime();

    @Override
    public int size() {
        return proxyList.size();
    }

    @Override
    public boolean isEmpty() {
        return proxyList.isEmpty();
    }

    protected synchronized void modified() {
        lastModify = System.nanoTime();
        try {
            //noinspection unchecked
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
    @Override
    public void forEachProxy(ForEachProxyHandler<T> action) throws Throwable {
        //log.info("forEachProxy, size:{}, elementData: {}", proxyList.size(), elementData);
        final Object[] elementData = this.elementData;
        if (elementData != null) {
            final int size = proxyList.size();
            for (int i = 0; i < size; i++) {
                //log.info("forEachProxy, callback: {}", elementData[i]);
                action.accept((T) elementData[i]);
            }
        } else {
            for (T t : this) {
                action.accept(t);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> Result<? extends R> forFirstProxy(ForFirstProxyHandler<? super T, ? extends Result<? extends R>> action) throws Throwable {
        final Object[] elementData = this.elementData;
        if (elementData != null) {
            final int size = proxyList.size();
            for (int i = 0; i < size; i++) {
                Result<? extends R> result = action.apply((T) elementData[i]);
                if (result != null && result.isSuccess()) {
                    return result;
                }
            }
        } else {
            for (T t : this) {
                Result<? extends R> result = action.apply(t);
                if (result != null && result.isSuccess()) {
                    return result;
                }
            }
        }
        return Result.failed();
    }

    @Override
    public String toString() {
        return "ListProxyHandler{" +
                "proxyList=" + proxyList +
                ", lastModify=" + lastModify +
                '}';
    }
}
