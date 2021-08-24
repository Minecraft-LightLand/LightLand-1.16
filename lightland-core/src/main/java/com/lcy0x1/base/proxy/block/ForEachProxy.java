package com.lcy0x1.base.proxy.block;

public @interface ForEachProxy {
    LoopProxyHandler handler = LoopProxyHandler.INSTANCE;

    Class<?>[] type() default Object.class;
}
