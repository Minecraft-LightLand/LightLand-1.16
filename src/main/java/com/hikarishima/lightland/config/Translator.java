package com.hikarishima.lightland.config;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.products.MagicProduct;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Translator {

    public static ITextComponent getContainer(String str) {
        return new TranslationTextComponent(LightLand.MODID + ":container." + str);
    }

    public static ITextComponent get(String str, Object... objs) {
        return new TranslationTextComponent(LightLand.MODID + ":" + str, objs);
    }

    public static <I extends IForgeRegistryEntry<I>, P extends MagicProduct<I, P>> ITextComponent get(MagicProduct<I, P> product) {
        return new TranslationTextComponent(product.type.namer.apply(product.item));
    }

    public static ITextComponent getDesc(MagicProduct<?, ?> product) {
        return get("magic_product." + product.type.getID() + "." + product.item.getRegistryName().toString());
    }
}
