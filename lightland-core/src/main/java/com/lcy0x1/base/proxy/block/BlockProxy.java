package com.lcy0x1.base.proxy.block;

import lombok.*;

import java.util.function.Consumer;
import java.util.function.Function;

public interface BlockProxy<T> {
    @Getter
    @ToString
    @AllArgsConstructor
    class Result<R> {
        public static final Result<?> failed = new Result<>(false, null);
        static final ThreadLocal<Result<?>> resultThreadLocal = new ThreadLocal<>();

        boolean success;
        R result;
    }

    static <R> Result<R> of(R result) {
        //noinspection unchecked
        Result<R> r = (Result<R>) Result.resultThreadLocal.get();
        if (r == null) {
            r = new Result<>(true, result);
            Result.resultThreadLocal.set(r);
        } else {
            r.result = result;
        }
        return r;
    }

    static <R> Result<R> failed() {
        //noinspection unchecked
        return (Result<R>) Result.failed;
    }

    void forEachProxy(Consumer<T> action);

    <R> Result<R> forFirstProxy(Function<T, Result<R>> action);
}
