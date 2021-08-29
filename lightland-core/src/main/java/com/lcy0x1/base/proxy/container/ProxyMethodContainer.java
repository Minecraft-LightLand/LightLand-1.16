package com.lcy0x1.base.proxy.container;

import com.lcy0x1.base.proxy.Result;
import com.lcy0x1.base.proxy.handler.ForEachProxyHandler;
import com.lcy0x1.base.proxy.handler.ForFirstProxyHandler;
import com.lcy0x1.base.proxy.handler.ProxyMethod;

public interface ProxyMethodContainer<T extends ProxyMethod> extends Iterable<T> {
    int size();

    boolean isEmpty();

    default void forEachProxy(ForEachProxyHandler<T> action) throws Throwable {
        for (T t : this) {
            action.accept(t);
        }
    }

    default <R> Result<? extends R> forFirstProxy(ForFirstProxyHandler<? super T, ? extends Result<? extends R>> action) throws Throwable {
        for (T t : this) {
            Result<? extends R> result = action.apply(t);
            if (result != null && result.isSuccess()) {
                return result;
            }
        }
        return Result.failed();
    }


    default long getLastModify() {
        return System.nanoTime();
    }
}
