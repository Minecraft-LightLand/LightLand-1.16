package com.hikarishima.lightland.magic.products.instance;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.recipe.IMagicRecipe;
import com.lcy0x1.core.util.NBTObj;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;

public class PotionMagic extends MagicProduct<Effect, PotionMagic> {

    public PotionMagic(MagicHandler player, NBTObj tag, ResourceLocation rl, IMagicRecipe<?> r) {
        super(MagicRegistry.MPT_EFF, player, tag, rl, r);
    }
}
