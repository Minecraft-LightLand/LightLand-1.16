package com.lcy0x1.base.proxy;

import lombok.*;

public interface Proxy<T extends ProxyMethod> extends Iterable<T> {
    default void forEachProxy(ForEachProxyHandler<T> action) throws Throwable {
        for (T t : this) {
            action.accept(t);
        }
    }

    default <R> Result<R> forFirstProxy(ForFirstProxyHandler<T, Result<R>> action) throws Throwable {
        for (T t : this) {
            Result<R> result = action.apply(t);
            if (result != null && result.isSuccess()) {
                return result;
            }
        }
        return Proxy.failed();
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

    @Getter
    @ToString
    @AllArgsConstructor
    class Result<R> {

        private static final Result<?> failed = new Result<>(false, null);

        private static final Result<?> main = new Result<>(true, null);
        private static final Result<?> NULL = new Result<>(true, null);
        private static final Thread mainThread = Thread.currentThread();
        private boolean success;
        @Setter(AccessLevel.PACKAGE)
        private R result;
    }

    @SuppressWarnings("unchecked")
    static <R> Result<R> failed() {
        return (Result<R>) Result.failed;
    }

    @SuppressWarnings("unchecked")
    static <R> Result<R> of() {
        return (Result<R>) Result.NULL;
    }

    @SuppressWarnings("unchecked")
    static <R> Result<R> of(R result) {
        if (Thread.currentThread() == Result.mainThread) {
            ((Result<R>) Result.main).setResult(result);
            return (Result<R>) Result.main;
        }
        return new Result<>(true, result);
    }

    static <R> Result<R> alloc(R result) {
        return new Result<>(true, result);
    }
}
