package com.lcy0x1.base.proxy;

import net.sf.cglib.proxy.CallbackFilter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ProxyCallbackFilter implements CallbackFilter {
    private final Set<Method> ignoreMethods = new HashSet<>();

    public ProxyCallbackFilter(Class<?>... ignoreClass) {
        for (Class<?> clazz : ignoreClass) {
            ignoreMethods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        }
    }

    @Override
    public int accept(Method method) {
        for (Method ignoreMethod : ignoreMethods) {
            if (Reflections.equalsMethod(method, ignoreMethod)) {
                return 1;
            }
        }
        return 0;
    }
}
