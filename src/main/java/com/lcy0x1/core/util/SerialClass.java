package com.lcy0x1.core.util;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface SerialClass {

    @Documented
    @Retention(RUNTIME)
    @Target(FIELD)
    public @interface SerialField {

        boolean toClient() default false;

        Class<?>[] generic() default {};

    }

    @Documented
    @Retention(RUNTIME)
    @Target(METHOD)
    public @interface OnInject {

    }

}
