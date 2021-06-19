package com.hikarishima.lightland.registry;

import com.lcy0x1.core.util.ExceptionHandler;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

public class RegistryBase {

    public static <T> void process(Class<?> provider, Class<T> reg, Consumer<T> acceptor) {
        ExceptionHandler.run(() -> {
            for (Field f : provider.getDeclaredFields())
                if ((f.getModifiers() & Modifier.STATIC) != 0 && reg.isAssignableFrom(f.getType()))
                    ((Consumer)acceptor).accept(f.get(null));
        });
    }

    public static void init(){

    }

}
