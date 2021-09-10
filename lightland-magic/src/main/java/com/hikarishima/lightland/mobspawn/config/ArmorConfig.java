package com.hikarishima.lightland.mobspawn.config;

import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.proxy.Proxy;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SerialClass
public class ArmorConfig {

    public static ArmorConfig getInstance() {
        return ConfigRecipe.getObject(Proxy.getWorld(), MagicRecipeRegistry.SPAWN, "armor");
    }

    public Item getItem(EquipmentSlotType type, MobEntity e, float level, Random r) {
        List<Entry> list = new ArrayList<>();
        int sum = 0;
        for (Entry entry : items) {
            if (entry.matches(type, e) && entry.level <= level) {
                list.add(entry);
                sum += entry.weight;
            }
        }
        if (sum == 0) {
            return null;
        }
        int rand = r.nextInt(sum);
        for (Entry entry : list) {
            if (rand < entry.weight) {
                return entry.item;
            }
            rand -= entry.weight;
        }
        return null;
    }

    public ItemStack getItemStack(EquipmentSlotType type, MobEntity e, float level, int enchant, Random r) {
        Item item = getItem(type, e, level, r);
        if (item == null) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = item.getDefaultInstance();
        EnchantmentHelper.enchantItem(r, stack, enchant, true);
        return stack;
    }

    private boolean fit(MobEntity entity) {
        return entity instanceof ZombieEntity ||
                entity instanceof AbstractRaiderEntity ||
                entity instanceof AbstractSkeletonEntity ||
                entity instanceof AbstractPiglinEntity;
    }

    public void fillEntity(MobEntity entity, float level, Random r) {
        if (!fit(entity))
            return;
        GeneralConfig gen = GeneralConfig.getInstance();
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            if (slot.getType() != EquipmentSlotType.Group.ARMOR)
                continue;
            if (!entity.getItemBySlot(slot).isEmpty())
                continue;
            if (r.nextDouble() > gen.armor_chance * level)
                continue;
            ItemStack stack = getItemStack(slot, entity, level, (int) (level * gen.enchant_factor), r);
            entity.setItemSlot(slot, stack);
            entity.setDropChance(slot, 1);
        }
    }

    @SerialClass.SerialField
    public Entry[] items;

    @SerialClass
    public static class Entry {

        @SerialClass.SerialField
        public float level;

        @SerialClass.SerialField
        public int weight;

        @SerialClass.SerialField
        public Item item;

        public boolean matches(EquipmentSlotType slot, MobEntity e) {
            if (slot.getType() == EquipmentSlotType.Group.ARMOR) {
                if (item instanceof ArmorItem) {
                    return ((ArmorItem) item).getSlot() == slot;
                }
                return false;
            }
            return false;
        }

    }

}
