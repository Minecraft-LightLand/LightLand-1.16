package com.hikarishima.lightland.magic.products.instance;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.recipe.IMagicRecipe;
import com.lcy0x1.core.util.NBTObj;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class CraftMagic extends MagicProduct<Item, CraftMagic> {

    public CraftMagic(MagicHandler player, NBTObj tag, ResourceLocation rl, IMagicRecipe<?> r) {
        super(MagicRegistry.MPT_CRAFT, player, tag, rl, r);
    }

}
