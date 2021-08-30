package com.hikarishima.lightland.magic.capabilities.weight;

import net.minecraft.inventory.EquipmentSlotType;

public interface IWeightedMaterial {

    /** unit: gram */
    int getWeight(EquipmentSlotType slot);

}
