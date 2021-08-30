package com.hikarishima.lightland.magic.registry.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class HeavyEnchantment extends Enchantment {

    public HeavyEnchantment() {
        super(Rarity.RARE, EnchantmentType.ARMOR, new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET});
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public int getMinCost(int lv) {
        return 1 + lv * 10;
    }

    @Override
    public int getMaxCost(int lv) {
        return 11 + lv * 10;
    }
}
