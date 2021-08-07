package com.hikarishima.lightland.config;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.proxy.Proxy;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

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

    @OnlyIn(Dist.CLIENT)
    public static List<ITextProperties> getDesc(MagicProduct<?, ?> product) {
        MagicHandler h = MagicHandler.get(Proxy.getClientPlayer());
        List<ITextProperties> list = new ArrayList<>();
        list.add(get("screen.magic_tree.status." + product.getState().toString()));
        if (!product.unlocked()) {
            for (IMagicRecipe.ElementalMastery em : product.recipe.elemental_mastery) {
                if (h.magicHolder.getElementalMastery(em.element) < em.level)
                    list.add(get("screen.magic_tree.elem.pre")
                            .append(em.element.getDesc())
                            .append(get("screen.magic_tree.elem.post", em.level))
                            .withStyle(TextFormatting.RED));
            }
        }
        if (product.usable()) {
            list.add(get("screen.magic_tree.cost", product.getCost()));
            MagicProduct.CodeState state = product.logged(h);
            if (state == MagicProduct.CodeState.SHORT)
                list.add(get("screen.magic_tree.tree.short"));
            if (state == MagicProduct.CodeState.REPEAT)
                list.add(get("screen.magic_tree.tree.repeat"));
        }
        return list;
    }

}
