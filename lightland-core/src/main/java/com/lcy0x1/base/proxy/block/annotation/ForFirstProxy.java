package com.lcy0x1.base.proxy.block.annotation;

public @interface ForFirstProxy {
    Class<?> type();

    /**
     * proxy objs must handle this method
     * or will throw an exception
     */
    boolean must() default false;

    String errMsg() default "";

    Class<? extends RuntimeException> errClass() default RuntimeException.class;
}
