package com.hikarishima.lightland.event.combat;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nullable;

public interface ISpecialWeapon {

    /**
     * process event dealt by this weapon
     * if this method does not need to alter damage source, return null
     * */
    @Nullable
    MagicDamageSource getSource(ItemStack stack, LivingHurtEvent event);

}
