package com.lcy0x1.base;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class NamedEntry<T extends NamedEntry<T>> extends ForgeRegistryEntry<T> {

    private final Supplier<IForgeRegistry<T>> registry;

    private String desc = null;

    private TranslationTextComponent trans = null;

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

    public TranslationTextComponent getDesc() {
        if (trans != null)
            return trans;
        return trans = new TranslationTextComponent(getDescriptionId());
    }

    public String getID() {
        return getRegistryName().toString();
    }

}
