package com.lcy0x1.base.proxy;

public class NoProxyFoundException extends RuntimeException {
    public NoProxyFoundException() {
        super("no proxy found");
    }

    public NoProxyFoundException(String message) {
        super(message);
    }

    public NoProxyFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoProxyFoundException(Throwable cause) {
        super(cause);
    }

    public NoProxyFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
