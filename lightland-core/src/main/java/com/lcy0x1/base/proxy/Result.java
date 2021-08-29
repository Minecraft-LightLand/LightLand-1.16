package com.lcy0x1.base.proxy;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("StaticInitializerReferencesSubClass")
@Getter
public class Result<R> {
    @NotNull
    public static final Result<?> failed = new StaticResult<>(false, null);
    private static final Result<?> main = new Result<>(true, null);
    private static final Result<?> NULL = alloc(null);
    private static final Result<Boolean> TRUE = alloc(true);
    private static final Result<Boolean> FALSE = alloc(false);
    private static final Thread mainThread = Thread.currentThread();

    @SuppressWarnings("unchecked")
    @NotNull
    public static <R> Result<R> failed() {
        return (Result<R>) Result.failed;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <R> Result<R> of() {
        return (Result<R>) Result.NULL;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <R> Result<R> of(R result) {
        if (Thread.currentThread() == Result.mainThread) {
            ((Result<R>) Result.main).setResult(result);
            return (Result<R>) Result.main;
        }
        return new StaticResult<>(true, result);
    }

    @NotNull
    public static Result<Boolean> of(boolean b) {
        return alloc(b);
    }

    @NotNull
    public static <R> Result<R> alloc(R result) {
        return new StaticResult<>(true, result);
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    public static Result<Boolean> alloc(boolean b) {
        if (b) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    @NotNull
    public static <R> Result<R> snapshot(@Nullable Result<R> result) {
        if (result == null) {
            return failed();
        } else {
            return result.snapshot();
        }
    }

    private final boolean success;
    private R result;
    private int hashCode;

    private Result(boolean success, R result) {
        this.success = success;
        this.result = result;
    }

    private void setResult(R result) {
        this.result = result;
        hashCode = 0;
    }

    @Override
    public String toString() {
        return "Result{" + "success=" + success + ", result=" + result + '}';
    }

    public Result<R> snapshot() {
        return new Result<>(success, result);
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

    private final static class StaticResult<R> extends Result<R> {
        public StaticResult(boolean success, R result) {
            super(success, result);
        }

        @Override
        public Result<R> snapshot() {
            return this;
        }
    }
}