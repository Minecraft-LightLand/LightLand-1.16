package com.lcy0x1.base.proxy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

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

    @Getter
    @ToString
    @AllArgsConstructor
    class Result<R> {
        public static final Result<?> failed = new Result<>(false, null);
        static final ThreadLocal<Result<?>> resultThreadLocal = new ThreadLocal<>();

        private boolean success;
        private R result;
    }

    static <R> Result<R> of() {
        return of(null);
    }

    /**
     * 返回一个临时使用的 Result 对象
     * 因为是临时对象，所以不要把这个对象放到任何当前函数堆栈以外的地方
     * 如果要长期储存对象请 new Result
     */
    static <R> Result<R> of(R result) {
        return new Result<>(true, result);
        ////noinspection unchecked
        //Result<R> r = (Result<R>) Result.resultThreadLocal.get();
        //if (r == null) {
        //    r = new Result<>(true, result);
        //    Result.resultThreadLocal.set(r);
        //} else {
        //    r.result = result;
        //}
        //return r;
    }

    static <R> Result<R> failed() {
        //noinspection unchecked
        return (Result<R>) Result.failed;
    }
}