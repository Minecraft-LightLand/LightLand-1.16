package com.lcy0x1.base.proxy.annotation;

import com.lcy0x1.base.proxy.container.WithinProxyContextConfig;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithinProxyContext {
    boolean block() default false;

    boolean proxy() default false;

    boolean pre() default false;

    boolean preSuper() default false;

    class Utils {
        @NotNull
        public static WithinProxyContextConfig toWithinProxyContextConfig(@NotNull WithinProxyContext withinProxyContext) {
            return new WithinProxyContextConfig(
                    withinProxyContext.block(),
                    withinProxyContext.proxy(),
                    withinProxyContext.pre(),
                    withinProxyContext.preSuper()
            );
        }

        public static WithinProxyContextConfig get(Object obj) {
            if (obj == null) {
                return null;
            }
            return get(obj.getClass());
        }

        public static WithinProxyContextConfig get(Method method) {
            if (method == null) {
                return null;
            }
            final WithinProxyContext withinProxyContext = method.getAnnotation(WithinProxyContext.class);
            if (withinProxyContext != null) {
                return toWithinProxyContextConfig(withinProxyContext);
            } else {
                return null;
            }
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
                return toWithinProxyContextConfig(withinProxyContextAnnotation);
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
