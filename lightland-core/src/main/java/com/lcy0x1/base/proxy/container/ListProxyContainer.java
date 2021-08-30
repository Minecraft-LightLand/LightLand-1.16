package com.lcy0x1.base.proxy.container;

import com.lcy0x1.base.proxy.annotation.Singleton;
import com.lcy0x1.base.proxy.handler.ProxyMethod;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.util.*;

@Log4j2
public class ListProxyContainer<T extends ProxyMethod> extends ListProxyHandler<T> implements MutableProxyContainer<T> {
    private final Map<Class<?>, Object> singletonMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public synchronized int addProxy(T proxy) {
        if (proxy == null) return -1;
        check(proxy);
        final int addProxy;
        if (proxy.onAdded((MutableProxyContainer<ProxyMethod>) this)) {
            addProxy = super.addProxy(proxy);
        } else {
            return -1;
        }
        return addProxy;
    }

    @Override
    public synchronized boolean addAllProxy(Collection<T> proxy) {
        for (T p : proxy) {
            addProxy(p);
        }
        return true;
    }

    @Override
    public synchronized boolean removeProxy(T proxy) {
        final boolean remove = super.removeProxy(proxy);
        if (remove) {
            removeSingleton(proxy);
        }
        return remove;
    }

    @Override
    public synchronized T removeProxy(int index) {
        if (index < 0) return null;
        final T remove = super.removeProxy(index);
        removeSingleton(remove);
        return remove;
    }

    protected synchronized void check(ProxyMethod obj) {
        if (obj == null) {
            return;
        }
        check(obj.getClass(), new HashSet<>(), obj);
    }

    private void check(Class<?> clazz, Set<Class<?>> note, Object obj) {
        if (clazz == null || clazz == Object.class || !note.add(clazz)) {
            return;
        }

        final Singleton singleton = clazz.getAnnotation(Singleton.class);
        if (singleton != null && singleton.enabled()) {
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
        removeSingleton(obj.getClass(), new HashSet<>());
    }

    protected void removeSingleton(Class<?> clazz, Set<Class<?>> note) {
        if (clazz == null || clazz == Object.class || !note.add(clazz)) {
            return;
        }
        if (clazz.getAnnotation(Singleton.class) != null) {
            singletonMap.remove(clazz);
        }
        removeSingleton(clazz.getSuperclass(), note);
        for (Class<?> anInterface : clazz.getInterfaces()) {
            removeSingleton(anInterface, note);
        }
    }
}
