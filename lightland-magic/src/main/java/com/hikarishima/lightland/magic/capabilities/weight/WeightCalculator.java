package com.hikarishima.lightland.magic.capabilities.weight;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;

public class WeightCalculator {

    public static int getTotalWeight(LivingEntity entity) {
        int ans = 0;
        for (ItemStack stack : entity.getArmorSlots()) {
            ans += getWeight(stack);
        }
        return ans;
    }

    public static int getWeight(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem) {
            ArmorItem armor = (ArmorItem) stack.getItem();
            if (armor.getMaterial() instanceof IWeightedMaterial) {
                int weight = ((IWeightedMaterial) armor.getMaterial()).getWeight(armor.getSlot());
                if (weight > 0)
                    return weight;
            }
            int slot_factor = getDefaultSlotFactor(armor.getSlot());
            int weight_factor = 0;
            int weight_extra = 0;
            if (armor.getMaterial() instanceof ArmorMaterial) {
                weight_factor = getWeightFactor((ArmorMaterial) armor.getMaterial());
                weight_extra = getWeightExtra((ArmorMaterial) armor.getMaterial());
            }
            int weight = slot_factor * weight_factor + weight_extra;
            if (weight > 0)
                return weight;
            return armor.getDefense() * 1000;
        }
        return 0;
    }

    private static int getWeightFactor(ArmorMaterial material) {
        if (material == ArmorMaterial.LEATHER) {
            return 30;
        } else if (material == ArmorMaterial.CHAIN) {
            return 50;
        } else if (material == ArmorMaterial.IRON) {
            return 70;
        } else if (material == ArmorMaterial.GOLD) {
            return 100;
        } else if (material == ArmorMaterial.DIAMOND) {
            return 80;
        } else if (material == ArmorMaterial.NETHERITE) {
            return 80;
        } else if (material == ArmorMaterial.TURTLE) {
            return 20;
        }
        return 0;
    }

    private static int getWeightExtra(ArmorMaterial material) {
        if (material == ArmorMaterial.NETHERITE)
            return 100;
        return 0;
    }

    public static int getDefaultSlotFactor(EquipmentSlotType slot) {
        if (slot == EquipmentSlotType.HEAD) {
            return 5;
        } else if (slot == EquipmentSlotType.CHEST) {
            return 8;
        } else if (slot == EquipmentSlotType.LEGS) {
            return 7;
        } else if (slot == EquipmentSlotType.FEET) {
            return 4;
        }
        return 0;
    }

    public static int[] getWeight(int mat_weight, int extra) {
        int[] ans = new int[4];
        for (int i = 0; i < 4; i++) {
            ans[i] = extra + mat_weight * getDefaultSlotFactor(
                    EquipmentSlotType.byTypeAndIndex(EquipmentSlotType.Group.ARMOR, i));
        }
        return ans;
    }

}
