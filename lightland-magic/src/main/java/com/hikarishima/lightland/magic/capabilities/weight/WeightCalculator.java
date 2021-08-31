package com.hikarishima.lightland.magic.capabilities.weight;

import com.hikarishima.lightland.magic.registry.VanillaMagicRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
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
        int weight = getItemWeight(stack);
        int lv = EnchantmentHelper.getItemEnchantmentLevel(VanillaMagicRegistry.ENCH_HEAVY, stack);
        return (int) (weight * (1 + 0.1 * lv));
    }

    private static int getItemWeight(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem) {
            ArmorItem armor = (ArmorItem) stack.getItem();
            int slot_factor = getSlotFactor(armor.getSlot());
            int weight_factor = 0;
            int weight_extra = 0;
            ArmorWeight config = ArmorWeight.getInstance();
            if (armor.getRegistryName() != null) {
                String id = armor.getRegistryName().toString();
                ArmorWeight.Entry entry = config.entries.get(id);
                if (entry == null) {
                    String cut = cut(id, config);
                    id = config.materials.get(id);
                    if (id == null)
                        id = cut;
                    entry = config.entries.get(id);
                }
                if (entry != null) {
                    weight_factor = entry.ingredient_weight;
                    weight_extra = entry.extra_weight;
                }
            }
            int weight = slot_factor * weight_factor + weight_extra;
            if (weight > 0)
                return weight;
            return armor.getDefense() * 1000;
        }
        return 0;
    }

    private static String cut(String id, ArmorWeight config) {
        for (String suf : config.suffixes) {
            if (id.endsWith("_" + suf))
                return id.substring(0, id.length() - 1 - suf.length());
        }
        return id;
    }

    public static int getSlotFactor(EquipmentSlotType slot) {
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

}
