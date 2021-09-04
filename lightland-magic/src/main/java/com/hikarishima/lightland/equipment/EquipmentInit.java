package com.hikarishima.lightland.equipment;

import net.darkhax.itemstages.Restriction;
import net.darkhax.itemstages.RestrictionManager;
import net.minecraft.item.ItemStack;
import shadows.apotheosis.deadly.affix.AffixHelper;
import shadows.apotheosis.deadly.affix.EquipmentType;
import shadows.apotheosis.deadly.affix.LootRarity;
import shadows.apotheosis.deadly.reload.AffixLootManager;

import java.util.Optional;
import java.util.Random;

public class EquipmentInit {

    private static final String[] TYPE = {"armor_", "shoot_", "sword_", "shield_", "axe_", "others_"};

    public static void init() {
        for (int i = 1; i <= 6; i++)
            for (int j = 0; j < 3; j++) {
                Restriction armor = new Restriction("lightland_" + TYPE[j] + i);
                int finalI = i;
                int finalJ = j;
                armor.restrict(e -> getLevel(e) >= finalI && getStageType(e) == finalJ);
                RestrictionManager.INSTANCE.addRestriction(armor);
            }
    }

    public static ItemStack determine(ItemStack stack) {
        int lv = stack.getOrCreateTag().getInt("lightland:undertermined");
        if (lv == 0)
            return stack;
        LootRarity rar = LootRarity.values()[lv - 1];
        return AffixLootManager.genLootItem(stack, new Random(), EquipmentType.getTypeFor(stack), rar);
    }

    public static int getLevel(ItemStack stack) {
        int lv0 = stack.getOrCreateTag().getInt("lightland:undertermined");
        int lv1 = Optional.ofNullable(AffixHelper.getRarity(stack)).map(Enum::ordinal).orElse(0);
        return Math.max(lv0, lv1);
    }

    public static int getStageType(ItemStack stack) {
        EquipmentType type = EquipmentType.getTypeFor(stack);
        if (type == EquipmentType.ARMOR) return 0;
        if (type == EquipmentType.RANGED) return 1;
        if (type == EquipmentType.SWORD) return 2;
        if (type == EquipmentType.SHIELD) return 3;
        if (type == EquipmentType.AXE) return 4;
        return 6;
    }

}
