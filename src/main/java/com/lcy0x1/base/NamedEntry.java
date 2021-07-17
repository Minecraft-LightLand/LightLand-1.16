package com.lcy0x1.base;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class NamedEntry<T extends NamedEntry<T>> extends ForgeRegistryEntry<T> {

    private final Supplier<IForgeRegistry<T>> registry;

    private String desc = null;

    public NamedEntry(Supplier<IForgeRegistry<T>> registry) {
        this.registry = registry;
    }

    public String getDescriptionId() {
        if (desc != null)
            return desc;
        ResourceLocation rl = getRegistryName();
        ResourceLocation reg = registry.get().getRegistryName();
        desc = reg.toString() + "." + rl.toString();
        return desc;
    }

}
