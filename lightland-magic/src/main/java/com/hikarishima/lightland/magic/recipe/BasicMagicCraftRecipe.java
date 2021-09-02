package com.hikarishima.lightland.magic.recipe;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.ResourceLocation;

@SerialClass
public class BasicMagicCraftRecipe extends AbstractMagicCraftRecipe<BasicMagicCraftRecipe> {

    public BasicMagicCraftRecipe(ResourceLocation id) {
        super(id, MagicRecipeRegistry.RSM_CRAFT.get());
    }
}
