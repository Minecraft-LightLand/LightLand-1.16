package com.hikarishima.lightland.magic;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.recipe.IMagicRecipe;
import com.lcy0x1.core.magic.HexDirection;
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
        return new TranslationTextComponent(LightLandMagic.MODID + ":container." + str);
    }

    public static IFormattableTextComponent get(String str, Object... objs) {
        return new TranslationTextComponent(LightLandMagic.MODID + ":" + str, objs);
    }

    public static IFormattableTextComponent get(boolean red, String str, Object... objs) {
        TranslationTextComponent ans = new TranslationTextComponent(LightLandMagic.MODID + ":" + str, objs);
        if (red)
            ans.withStyle(TextFormatting.RED);
        return ans;
    }

    public static <I extends IForgeRegistryEntry<I>, P extends MagicProduct<I, P>> IFormattableTextComponent get(MagicProduct<I, P> product) {
        return new TranslationTextComponent(product.type.namer.apply(product.item));
    }

    public static IFormattableTextComponent get(HexDirection dire) {
        return get("screen.hex.dire." + dire.name().toLowerCase());
    }

    @OnlyIn(Dist.CLIENT)
    public static List<ITextProperties> getDesc(MagicProduct<?, ?> product) {
        MagicHandler h = MagicProxy.getHandler();
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

    private static final String[] NUMBERS = {"0", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};

    public static String getNumber(int i) {
        if (i < 0 || i >= NUMBERS.length)
            return "" + i;
        return NUMBERS[i];
    }

}
