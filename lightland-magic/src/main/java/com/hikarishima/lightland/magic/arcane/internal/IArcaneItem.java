package com.hikarishima.lightland.magic.arcane.internal;

import com.hikarishima.lightland.magic.registry.item.combat.IGlowingTarget;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IArcaneItem extends IGlowingTarget {

    int getMaxMana(ItemStack stack);

    @OnlyIn(Dist.CLIENT)
    default int getDistance(ItemStack stack){
        return 64;
    }

}
