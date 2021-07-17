package com.hikarishima.lightland.mobspawn;

import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class EquipLevel {

    public static List<EquipItem> ITEMS = new ArrayList<>();
    public static List<Enchant> ENCHANTS = new ArrayList<>();
    public static double ITEM_BUDGET = 0.2, ENCHANT_BUDGET = 0.2;

    public static double modify(IWorld world, LivingEntity ent, double difficulty) {
        if (ent instanceof ZombieEntity || ent instanceof AbstractSkeletonEntity) {
            // get a list of items
            double max_cost = ITEM_BUDGET * difficulty;

            EquipItem.Type weapon;
            if (ent instanceof WitherSkeletonEntity)
                weapon = EquipItem.Type.SWORD;
            else if (ent instanceof AbstractSkeletonEntity)
                weapon = EquipItem.Type.BOW;
            else if (ent instanceof DrownedEntity)
                weapon = EquipItem.Type.TRIDENT;
            else weapon = EquipItem.Type.SWORD;
            List<EquipItem.Type> type_list = new ArrayList<>();
            type_list.add(EquipItem.Type.HELMET);
            type_list.add(EquipItem.Type.CHESTPLATE);
            type_list.add(EquipItem.Type.LEGGINGS);
            type_list.add(EquipItem.Type.BOOTS);
            type_list.add(weapon);
            List<EquipItem> equips = new ArrayList<>();
            for (EquipItem equip : ITEMS) {
                if (equip.item != null && type_list.contains(equip.type) && equip.cost < max_cost) {
                    equips.add(equip);
                }
            }
            if (equips.size() == 0)
                return 0;
            List<EquipItem> items = IMobLevel.loot(world, equips, max_cost);
            for (EquipItem e : items)
                max_cost -= e.cost;
            List<ItemPair> stacks = new ArrayList<>();
            for (EquipItem e : items)
                stacks.add(new ItemPair(new ItemStack(e.item), e));

            // get a list of enchants
            max_cost += ENCHANT_BUDGET * difficulty;
            List<EnchantPair> pair = new ArrayList<>();
            for (Enchant equip : ENCHANTS) {
                if (equip.enchantment != null && equip.cost < max_cost) {
                    for (ItemPair is : stacks)
                        if (equip.enchantment.canEnchant(is.stack))
                            pair.add(new EnchantPair(is.stack, equip));
                }
            }
            List<EnchantPair> selected_enc = IMobLevel.loot(world, pair, max_cost);

            double cost = 0;
            for (ItemPair is : stacks) {
                cost += is.equip.cost;
                for (EnchantPair enc : selected_enc) {
                    if (enc.stack == is.stack) {
                        is.stack.enchant(enc.equip.enchantment, enc.equip.level);
                        cost += enc.equip.cost;
                    }
                }
                ent.setItemSlot(is.equip.type.type, is.stack);
            }
            return cost;
        }
        return 0;
    }

    @SerialClass
    public static class Enchant {

        @SerialClass.SerialField
        public String id;

        @SerialClass.SerialField
        public double cost, chance = 1;

        @SerialClass.SerialField
        public int weight, level;

        public Enchantment enchantment;

        @SerialClass.OnInject
        public void onInject() {
            enchantment = ExceptionHandler.get(() -> ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(id)));
        }

    }

    @SerialClass
    public static class EquipItem implements IMobLevel.Entry<EquipItem> {

        @SerialClass.SerialField
        public Type type;
        @SerialClass.SerialField
        public String id;
        @SerialClass.SerialField
        public double cost, chance = 1;
        @SerialClass.SerialField
        public int weight;
        public Item item;

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public double getCost() {
            return cost;
        }

        @Override
        public boolean equal(EquipItem other) {
            return other.type == type;
        }

        @Override
        public double getChance() {
            return chance;
        }

        @SerialClass.OnInject
        public void onInject() {
            item = ExceptionHandler.get(() -> ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(id)));
        }

        public enum Type {
            HELMET(EquipmentSlotType.HEAD),
            CHESTPLATE(EquipmentSlotType.CHEST),
            LEGGINGS(EquipmentSlotType.LEGS),
            BOOTS(EquipmentSlotType.FEET),
            SWORD(EquipmentSlotType.MAINHAND),
            BOW(EquipmentSlotType.MAINHAND),
            TRIDENT(EquipmentSlotType.MAINHAND);

            public EquipmentSlotType type;

            Type(EquipmentSlotType type) {
                this.type = type;
            }
        }

    }

    public static class ItemPair {

        public ItemStack stack;
        public EquipItem equip;

        public ItemPair(ItemStack stack, EquipItem equip) {
            this.stack = stack;
            this.equip = equip;
        }
    }

    public static class EnchantPair implements IMobLevel.Entry<EnchantPair> {

        public ItemStack stack;
        public Enchant equip;

        public EnchantPair(ItemStack stack, Enchant equip) {
            this.stack = stack;
            this.equip = equip;
        }

        @Override
        public int getWeight() {
            return equip.weight;
        }

        @Override
        public double getCost() {
            return equip.cost;
        }

        @Override
        public boolean equal(EnchantPair other) {
            return other.stack == stack;
        }

        @Override
        public double getChance() {
            return equip.chance;
        }
    }
}
