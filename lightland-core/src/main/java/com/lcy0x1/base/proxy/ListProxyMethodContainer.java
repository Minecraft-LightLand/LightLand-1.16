package com.lcy0x1.base.proxy;

import com.lcy0x1.base.proxy.annotation.Singleton;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.*;

@Log4j2
public class ListProxyMethodContainer<T extends ProxyMethod> implements MutableProxyMethodContainer<T> {
    @NotNull
    @Getter
    private final ArrayList<T> proxyList;
    private Object[] elementData = null;
    @Getter
    private volatile long lastModify = 0;
    private Map<Class<?>, Object> singletonMap = new HashMap<>();

    public ListProxyMethodContainer() {
        proxyList = new ArrayList<>();
    }

    //public ListProxy(@NotNull List<T> proxyList) {
    //    this.proxyList = proxyList;
    //}

    protected synchronized void modified() {
        lastModify = System.currentTimeMillis();
        try {
            elementData = Reflections.getElementData(proxyList);
        } catch (Exception ignored) {
        }
    }

    @Override
    public synchronized int addProxy(T proxy) {
        check(proxy);
        lastModify = System.currentTimeMillis();
        proxyList.add(proxy);
        modified();
        return proxyList.size() - 1;
    }

    @Override
    public synchronized boolean addAllProxy(Collection<T> proxy) {
        for (T p : proxy) {
            check(p);
        }
        lastModify = System.currentTimeMillis();
        final boolean addAll = proxyList.addAll(proxy);
        modified();
        return addAll;
    }

    @Override
    public synchronized void removeProxy(T proxy) {
        lastModify = System.currentTimeMillis();
        removeSingleton(proxyList.remove(proxy));
        modified();
    }

    @Override
    public synchronized void removeProxy(int index) {
        lastModify = System.currentTimeMillis();
        removeSingleton(proxyList.remove(index));
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
        final Object[] elementData = this.elementData;
        if (elementData != null) {
            final int size = proxyList.size();
            for (int i = 0; i < size; i++) {
                action.accept((T) elementData[i]);
            }
        } else {
            MutableProxyMethodContainer.super.forEachProxy(action);
        }
    }

    protected synchronized void check(Object obj) {
        if (obj == null) {
            return;
        }
        check(obj.getClass(), new HashSet<>(), obj);
    }

    private void check(Class<?> clazz, Set<Class<?>> note, Object obj) {
        if (clazz == Object.class || !note.add(clazz)) {
            return;
        }

        final Singleton singleton = clazz.getAnnotation(Singleton.class);
        if (singleton != null) {
            if (singletonMap.get(clazz) == null) {
                singletonMap.put(clazz, obj);
            } else {
                try {
                    final Constructor<? extends RuntimeException> constructor = singleton.errClass().getConstructor(String.class);
                    String errMsg = singleton.errMsg();
                    if (StringUtils.isBlank(errMsg)) {
                        errMsg = "";
                    }

                    String[] args = new String[Singleton.errMsgTemplate.length];
                    if (errMsg.contains(Singleton.errMsgTemplate[0])) {
                        args[0] = obj.toString();
                    }
                    if (errMsg.contains(Singleton.errMsgTemplate[1])) {
                        args[1] = clazz.toString();
                    }

                    errMsg = StringUtils.replaceEach(errMsg, Singleton.errMsgTemplate, args);
                    constructor.newInstance(errMsg);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        check(clazz.getSuperclass(), note, obj);
        for (Class<?> anInterface : clazz.getInterfaces()) {
            check(anInterface, note, obj);
        }
    }

    protected synchronized void removeSingleton(Object obj) {
        if (obj == null) {
            return;
        }
        removeSingleton(obj.getClass(), new HashSet<>(), obj);
    }

    protected void removeSingleton(Class<?> clazz, Set<Class<?>> note, Object obj) {
        if (clazz == Object.class || !note.add(clazz)) {
            return;
        }
        if (clazz.getAnnotation(Singleton.class) != null) {
            singletonMap.remove(clazz);
        }
        removeSingleton(clazz.getSuperclass(), note, obj);
        for (Class<?> anInterface : clazz.getInterfaces()) {
            removeSingleton(anInterface, note, obj);
        }
    }
}
