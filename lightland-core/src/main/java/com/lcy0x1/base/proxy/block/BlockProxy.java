package com.lcy0x1.base.proxy.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.function.Consumer;
import java.util.function.Function;

public interface BlockProxy<T> {
    @Getter
    @ToString
    @AllArgsConstructor
    class Result<R> {
        public static final Result<?> failed = new Result<>(false, null);

        final boolean success;
        final R result;
    }

    static <R> Result<R> of(R result) {
        return new Result<>(true, result);
    }

    static <R> Result<R> failed() {
        //noinspection unchecked
        return (Result<R>) Result.failed;
    }

    void forEachProxy(Consumer<T> action);

    <R> Result<R> forFirstProxy(Function<T, Result<R>> action);
}
