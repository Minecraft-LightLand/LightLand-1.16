package com.hikarishima.lightland.registry;

import com.lcy0x1.core.util.ExceptionHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

public class RegistryBase {

    public static <T> void process(Class<?> provider, Class<T> reg, Consumer<T> acceptor) {
        ExceptionHandler.run(() -> {
            for (Field f : provider.getDeclaredFields())
                if ((f.getModifiers() & Modifier.STATIC) != 0)
                    if (reg.isAssignableFrom(f.getType()))
                        ((Consumer) acceptor).accept(f.get(null));
                    else if (f.getType().isArray() && reg.isAssignableFrom(f.getType().getComponentType()))
                        for (Object o : (Object[]) f.get(null))
                            ((Consumer) acceptor).accept(o);
        });
    }

    public static void init() {

    }

}
