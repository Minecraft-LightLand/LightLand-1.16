package com.hikarishima.lightland.magic;

import com.lcy0x1.core.util.NBTObj;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;

public class PotionMagic extends MagicProduct<Effect, PotionMagic> {

    public PotionMagic(NBTObj tag, ResourceLocation rl) {
        super(MagicRegistries.MPT_EFF, tag, rl);
    }
}
