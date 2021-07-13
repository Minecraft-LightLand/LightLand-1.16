package com.hikarishima.lightland.magic;

import com.lcy0x1.core.util.NBTObj;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;

public class EnchantmentMagic extends MagicProduct<Enchantment, EnchantmentMagic> {

    public EnchantmentMagic(NBTObj nbtManager, ResourceLocation rl){
        super(MagicRegistries.MPT_ENCH, nbtManager, rl);
    }

}
