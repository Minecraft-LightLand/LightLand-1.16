package com.lcy0x1.base.proxy.block;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class LoopProxyHandler {
    public static final LoopProxyHandler INSTANCE = new LoopProxyHandler();

    private LoopProxyHandler() {
    }

    private Map<Class<?>, Consumer<?>> handler = new ConcurrentHashMap<>();

    void registerHandler(Class<?> clazz, Consumer<?> handler) {
    }
}
