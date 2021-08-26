package com.lcy0x1.base.proxy;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@Log4j2
public class ListProxy<T extends ProxyMethod> implements MutableProxy<T> {
    private static Field elementDataField = null;

    static {
        try {
            elementDataField = ArrayList.class.getDeclaredField("elementData");
            elementDataField.setAccessible(true);
        } catch (Throwable ignored) {
        }
    }

    @NotNull
    @Getter
    private final ArrayList<T> proxyList;
    private Object[] elementData;
    @Getter
    private long lastModify = 0;

    public ListProxy() {
        proxyList = new ArrayList<>();
    }

    //public ListProxy(@NotNull List<T> proxyList) {
    //    this.proxyList = proxyList;
    //}

    protected void modified() {
        lastModify = System.currentTimeMillis();
        if (elementDataField != null) {
            try {
                elementData = (Object[]) elementDataField.get(proxyList);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public synchronized int addProxy(T proxy) {
        lastModify = System.currentTimeMillis();
        proxyList.add(proxy);
        modified();
        return proxyList.size() - 1;
    }

    @Override
    public synchronized boolean addAllProxy(Collection<T> proxy) {
        lastModify = System.currentTimeMillis();
        final boolean addAll = proxyList.addAll(proxy);
        modified();
        return addAll;
    }

    @Override
    public synchronized void removeProxy(T proxy) {
        lastModify = System.currentTimeMillis();
        proxyList.remove(proxy);
        modified();
    }

    @Override
    public synchronized void removeProxy(int index) {
        lastModify = System.currentTimeMillis();
        proxyList.remove(index);
        modified();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return proxyList.iterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void forEachProxy(ForEachProxyHandler<T> action) throws Throwable {
        if (elementData != null) {
            for (int i = 0; i < proxyList.size(); i++) {
                action.accept((T) elementData[i]);
            }
        } else {
            MutableProxy.super.forEachProxy(action);
        }
    }
}
