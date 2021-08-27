package com.lcy0x1.base.proxy.annotation;

import com.lcy0x1.base.proxy.container.WithinProxyContextConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithinProxyContext {
    boolean block() default false;

    boolean proxy() default false;


    class Utils {
        public static WithinProxyContextConfig get(Object obj) {
            if (obj == null) {
                return null;
            }
            return get(obj.getClass());
        }

        public static WithinProxyContextConfig get(Class<?> clazz) {
            return get(clazz, new HashSet<>());
        }

        private static WithinProxyContextConfig get(Class<?> clazz, Set<Class<?>> note) {
            if (clazz == null || clazz == Object.class || !note.add(clazz)) {
                return null;
            }

            WithinProxyContext withinProxyContextAnnotation = clazz.getAnnotation(WithinProxyContext.class);
            if (withinProxyContextAnnotation != null) {
                return new WithinProxyContextConfig(withinProxyContextAnnotation.block(), withinProxyContextAnnotation.proxy());
            }

            WithinProxyContextConfig withinProxyContext = get(clazz.getSuperclass());
            if (withinProxyContext != null) {
                return withinProxyContext;
            }
            for (Class<?> anInterface : clazz.getInterfaces()) {
                withinProxyContext = get(anInterface);
                if (withinProxyContext != null) {
                    return withinProxyContext;
                }
            }
            return null;
        }
    }
}
