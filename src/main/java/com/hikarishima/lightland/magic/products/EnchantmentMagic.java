package com.hikarishima.lightland.magic.products;

import com.hikarishima.lightland.magic.MagicProduct;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.lcy0x1.core.util.NBTObj;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

public class EnchantmentMagic extends MagicProduct<Enchantment, EnchantmentMagic> {

    public EnchantmentMagic(PlayerEntity player, NBTObj nbtManager, ResourceLocation rl){
        super(MagicRegistry.MPT_ENCH, player, nbtManager, rl);
    }

}
