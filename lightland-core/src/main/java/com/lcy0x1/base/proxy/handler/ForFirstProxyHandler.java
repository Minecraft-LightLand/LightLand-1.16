package com.lcy0x1.base.proxy.handler;

public interface ForFirstProxyHandler<T, R> {
    R apply(T t) throws Throwable;
}