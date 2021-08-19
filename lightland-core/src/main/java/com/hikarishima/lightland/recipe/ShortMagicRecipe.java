package com.hikarishima.lightland.recipe;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.ResourceLocation;

@SerialClass
public class ShortMagicRecipe extends IMagicRecipe<ShortMagicRecipe> {

    public ShortMagicRecipe(ResourceLocation id) {
        super(id, RecipeRegistry.RSM_SHORT);
    }
}
