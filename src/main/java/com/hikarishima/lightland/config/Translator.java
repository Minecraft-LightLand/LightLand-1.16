package com.hikarishima.lightland.config;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.products.MagicProduct;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Translator {

    public static IFormattableTextComponent getContainer(String str) {
        return new TranslationTextComponent(LightLand.MODID + ":container." + str);
    }

    public static IFormattableTextComponent get(String str, Object... objs) {
        return new TranslationTextComponent(LightLand.MODID + ":" + str, objs);
    }

    public static <I extends IForgeRegistryEntry<I>, P extends MagicProduct<I, P>> IFormattableTextComponent get(MagicProduct<I, P> product) {
        return new TranslationTextComponent(product.type.namer.apply(product.item));
    }

    public static IFormattableTextComponent getDesc(MagicProduct<?, ?> product) {
        return get("magic_product." + product.type.getID() + "." + product.item.getRegistryName().toString());
    }

}
