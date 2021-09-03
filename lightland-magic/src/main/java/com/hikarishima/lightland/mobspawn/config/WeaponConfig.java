package com.hikarishima.lightland.mobspawn.config;

import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.proxy.Proxy;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@SerialClass
public class WeaponConfig {

    public static WeaponConfig getInstance() {
        return ConfigRecipe.getObject(Proxy.getWorld(), MagicRecipeRegistry.SPAWN, "weapon");
    }

    public Item getItem(Type type, float level, Random r) {
        List<Entry> list = new ArrayList<>();
        int sum = 0;
        for (Entry entry : items.get(type.name())) {
            if (entry.level <= level) {
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

    public ItemStack getItemStack(Type type, float level, int enchant, Random r) {
        Item item = getItem(type, level, r);
        if (item == null) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = item.getDefaultInstance();
        EnchantmentHelper.enchantItem(r, stack, enchant, true);
        return stack;
    }

    private Type getType(MobEntity entity) {
        if (entity instanceof WitherSkeletonEntity)
            return Type.MEELEE;
        if (entity instanceof AbstractSkeletonEntity)
            return Type.BOW;
        if (entity instanceof DrownedEntity)
            return Type.TRIDENT;
        if (entity instanceof ZombieEntity)
            return Type.MEELEE;
        if (entity instanceof PiglinEntity)
            return Type.CROSSBOW;
        if (entity instanceof PillagerEntity)
            return Type.CROSSBOW;
        return null;
    }

    public void fillEntity(MobEntity entity, float level, Random r) {
        GeneralConfig gen = GeneralConfig.getInstance();
        EquipmentSlotType slot = EquipmentSlotType.MAINHAND;
        if (!entity.getItemBySlot(slot).isEmpty())
            return;
        Type type = getType(entity);
        if (type == null) {
            return;
        }
        if (r.nextDouble() > gen.weapon_chance.get(type.name()) * level)
            return;
        ItemStack stack = getItemStack(type, level, (int) (level * gen.enchant_factor), r);
        entity.setItemSlot(slot, stack);
        entity.setDropChance(slot, 1);

    }

    @SerialClass.SerialField(generic = {String.class, WeaponConfig.Entry[].class})
    public HashMap<String, Entry[]> items = new HashMap<>();

    @SerialClass
    public static class Entry {

        @SerialClass.SerialField
        public float level;

        @SerialClass.SerialField
        public int weight;

        @SerialClass.SerialField
        public Item item;

    }

    public enum Type {
        MEELEE, BOW, TRIDENT, CROSSBOW
    }

}
