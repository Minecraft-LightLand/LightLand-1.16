package com.lcy0x1.base.proxy;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public interface DelegatedProxyMethodContainer<T extends ProxyMethod> extends ProxyMethodContainer<T> {
    @NotNull
    ProxyMethodContainer<T> getProxy();

    @Override
    default void forEachProxy(ForEachProxyHandler<T> action) throws Throwable {
        getProxy().forEachProxy(action);
    }

    @Override
    default <R> Result<? extends R> forFirstProxy(ForFirstProxyHandler<? super T, ? extends Result<? extends R>> action) throws Throwable {
        return getProxy().forFirstProxy(action);
    }

    @Override
    default long getLastModify() {
        return getProxy().getLastModify();
    }

    @NotNull
    @Override
    default Iterator<T> iterator() {
        return getProxy().iterator();
    }

    @Override
    default void forEach(Consumer<? super T> action) {
        getProxy().forEach(action);
    }

    @Override
    default Spliterator<T> spliterator() {
        return getProxy().spliterator();
    }
}
