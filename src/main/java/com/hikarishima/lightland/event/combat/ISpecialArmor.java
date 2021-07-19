package com.hikarishima.lightland.event.combat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public interface ISpecialArmor {

    /**
     * return negative value for reduction
     * */
    float modifier(LivingEntity owner, DamageSource source, float original);

}
