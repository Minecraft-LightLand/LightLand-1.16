package com.lcy0x1.base.proxy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForFirstProxy {
    Class<?>[] value() default {};

    String name() default "";

    boolean cache() default true;

    /**
     * proxy objs must handle this method
     * or will throw an exception
     */
    boolean must() default false;

    String errMsg() default "";

    Class<? extends RuntimeException> errClass() default RuntimeException.class;
}
