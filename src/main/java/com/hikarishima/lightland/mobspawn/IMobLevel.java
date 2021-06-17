package com.hikarishima.lightland.mobspawn;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.IWorld;

public interface IMobLevel {

    double POTION_PART = 0.33, FAIL_PARTIAL = 0.2;

    static boolean apply(IWorld world, LivingEntity ent, double difficulty) {
        double def = difficulty;
        difficulty -= EquipLevel.modify(world, ent, difficulty);
        difficulty -= PotionLevel.modify(world, ent, difficulty * POTION_PART);
        difficulty -= BuffLevel.modify(world, ent, difficulty);
        return true;
    }

}
