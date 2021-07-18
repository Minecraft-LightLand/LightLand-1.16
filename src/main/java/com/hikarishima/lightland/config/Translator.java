package com.hikarishima.lightland.config;

import com.hikarishima.lightland.LightLand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class Translator {

    public static ITextComponent getContainer(String str) {
        return new TranslationTextComponent(LightLand.MODID + ":container." + str);
    }

    public static ITextComponent get(String str, Object... objs) {
        return new TranslationTextComponent(LightLand.MODID + ":" + str, objs);
    }

}
