package com.hikarishima.lightland.config;

import com.hikarishima.lightland.LightLand;
import com.lcy0x1.base.NamedEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Translator {

    public static ITextComponent getContainer(String str) {
        return new TranslationTextComponent(LightLand.MODID + ":container." + str);
    }

    public static ITextComponent get(String str, Object... objs) {
        for (int i = 0; i < objs.length; i++) {
            if (objs[i] instanceof NamedEntry<?>) {
                String name = ((NamedEntry<?>) objs[i]).getDescriptionId();
                objs[i] = new TranslationTextComponent(name);
            }
        }
        return new TranslationTextComponent(LightLand.MODID + ":" + str, objs);
    }

}
