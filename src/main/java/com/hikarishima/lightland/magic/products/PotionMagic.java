package com.hikarishima.lightland.magic.products;

import com.hikarishima.lightland.magic.MagicProduct;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.lcy0x1.core.util.NBTObj;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;

public class PotionMagic extends MagicProduct<Effect, PotionMagic> {

    public PotionMagic(PlayerEntity player, NBTObj tag, ResourceLocation rl) {
        super(MagicRegistry.MPT_EFF, player, tag, rl);
    }
}
