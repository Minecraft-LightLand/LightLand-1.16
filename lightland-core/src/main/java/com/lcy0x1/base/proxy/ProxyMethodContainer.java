package com.lcy0x1.base.proxy;

public interface ProxyMethodContainer<T extends ProxyMethod> extends Iterable<T> {
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

    interface ForEachProxyHandler<T extends ProxyMethod> {
        void accept(T t) throws Throwable;
    }

    interface ForFirstProxyHandler<T, R> {
        R apply(T t) throws Throwable;
    }


    default long getLastModify() {
        return System.currentTimeMillis();
    }
}
