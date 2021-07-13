package com.hikarishima.lightland.recipe;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.ResourceLocation;

@SerialClass
public class DefMagicRecipe extends IMagicRecipe<DefMagicRecipe> {



    public DefMagicRecipe(ResourceLocation id) {
        super(id, RecipeRegistry.RSM_DEF);
    }

}
