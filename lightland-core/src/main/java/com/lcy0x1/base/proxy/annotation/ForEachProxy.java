package com.lcy0x1.base.proxy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForEachProxy {
    Class<?>[] value() default {};

    String name() default "";

    boolean keepContext() default false;

    LoopType type() default LoopType.BEFORE;

    enum LoopType {
        BEFORE, // loop and call proxied instance
        AFTER, // loop and return last proxy return
        BEFORE_WITH_RETURN, // loop and return without call proxied instance
    }
}
