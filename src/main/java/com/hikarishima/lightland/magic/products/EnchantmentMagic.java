package com.hikarishima.lightland.magic.products;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.lcy0x1.core.util.NBTObj;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;

public class EnchantmentMagic extends MagicProduct<Enchantment, EnchantmentMagic> {

    public EnchantmentMagic(MagicHandler player, NBTObj nbtManager, ResourceLocation rl, IMagicRecipe<?> r) {
        super(MagicRegistry.MPT_ENCH, player, nbtManager, rl, r);
    }

}
