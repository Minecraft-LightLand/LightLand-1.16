package com.lcy0x1.base.proxy.handler;

public interface ForEachProxyHandler<T> {
    void accept(T t) throws Throwable;
}