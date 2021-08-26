package com.lcy0x1.base.proxy;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
public class Result<R> {
    private static final Result<?> failed = new Result<>(false, null);
    private static final Result<?> main = new Result<>(true, null);
    private static final Result<?> NULL = new Result<>(true, null);
    private static final Thread mainThread = Thread.currentThread();

    private boolean success;
    @Setter(AccessLevel.PACKAGE)
    private R result;


    @SuppressWarnings("unchecked")
    public static <R> Result<R> failed() {
        return (Result<R>) Result.failed;
    }

    @SuppressWarnings("unchecked")
    public static <R> Result<R> of() {
        return (Result<R>) Result.NULL;
    }

    @SuppressWarnings("unchecked")
    public static <R> Result<R> of(R result) {
        if (Thread.currentThread() == Result.mainThread) {
            ((Result<R>) Result.main).setResult(result);
            return (Result<R>) Result.main;
        }
        return new Result<>(true, result);
    }

    public static <R> Result<R> alloc(R result) {
        return new Result<>(true, result);
    }
}