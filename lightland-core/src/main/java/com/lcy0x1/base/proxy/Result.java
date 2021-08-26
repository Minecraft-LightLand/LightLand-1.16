package com.lcy0x1.base.proxy;

import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public final class Result<R> {
    private static final Result<?> failed = new Result<>(false, null);
    private static final Result<?> main = new Result<>(true, null);
    private static final Result<?> NULL = new Result<>(true, null);
    private static final Result<Boolean> TRUE = new Result<>(true, true);
    private static final Result<Boolean> FALSE = new Result<>(true, false);
    private static final Thread mainThread = Thread.currentThread();

    private final boolean success;
    private R result;
    private int hashCode;


    public Result(boolean success, R result) {
        this.success = success;
        this.result = result;
    }

    void setResult(R result) {
        this.result = result;
        hashCode = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Result)) return false;
        Result<?> result1 = (Result<?>) o;
        return success == result1.success && Objects.equals(result, result1.result);
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = Objects.hash(success, result, hashCode);
        }
        return hashCode;
    }

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

    public static Result<Boolean> of(boolean b) {
        return alloc(b);
    }

    public static <R> Result<R> alloc(R result) {
        return new Result<>(true, result);
    }

    public static Result<Boolean> alloc(boolean b) {
        if (b) {
            return TRUE;
        } else {
            return FALSE;
        }
    }
}