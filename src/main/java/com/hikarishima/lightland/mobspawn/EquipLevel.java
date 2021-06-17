package com.hikarishima.lightland.mobspawn;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class EquipLevel {

    public static List<Equip> ENTRIES = new ArrayList<>();
    public static double EQUIP = 0.2, ENCHANT = 0.2;

    public static void init() {
        ENTRIES.clear();
        String path = FMLPaths.CONFIGDIR.get().toString();
        File file = new File(path + File.separator + "lightland" + File.separator + "equipment_cost.json");
        if (file.exists()) {
            JsonElement elem = ExceptionHandler.get(() -> new JsonParser().parse(new FileReader(file)));
            if (elem != null && elem.isJsonArray()) {
                for (JsonElement e : elem.getAsJsonArray()) {
                    ENTRIES.add(Serializer.from(e.getAsJsonObject(), Equip.class, new Equip()));
                }
            }
        } else {
            LogManager.getLogger().warn(file.toString() + " does not exist");
        }
    }

    @SerialClass
    public static class Equip {

        public enum Type {
            HELMET(EquipmentSlotType.HEAD),
            CHESTPLATE(EquipmentSlotType.CHEST),
            LEGGINGS(EquipmentSlotType.LEGS),
            BOOTS(EquipmentSlotType.FEET),
            SWORD(EquipmentSlotType.MAINHAND),
            BOW(EquipmentSlotType.MAINHAND),
            TRIDENT(EquipmentSlotType.MAINHAND),
            ENCHANTMENT(null);

            public EquipmentSlotType type;

            Type(EquipmentSlotType type) {
                this.type = type;
            }
        }

        @SerialClass.SerialField
        public Type type;

        @SerialClass.SerialField
        public String id;

        @SerialClass.SerialField
        public double cost, chance;

        @SerialClass.SerialField
        public int weight, level;

        public Item item;
        public Enchantment enchantment;

        @SerialClass.OnInject
        public void onInject() {
            if (type == Type.ENCHANTMENT) {
                enchantment = ExceptionHandler.get(() -> ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(id)));
            } else {
                item = ExceptionHandler.get(() -> ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(id)));
            }
        }

    }

    public static class EnchantPair {

        public ItemStack stack;
        public Equip equip;

        public EnchantPair(ItemStack stack, Equip equip) {
            this.stack = stack;
            this.equip = equip;
        }

    }

    public static double modify(IWorld world, LivingEntity ent, double difficulty) {
        if (ent instanceof ZombieEntity || ent instanceof AbstractSkeletonEntity) {
            // get a list of items
            double max_cost = EQUIP * difficulty;
            List<EnchantPair> items = new ArrayList<>();
            {
                Equip.Type weapon;
                if (ent instanceof WitherSkeletonEntity)
                    weapon = Equip.Type.SWORD;
                else if (ent instanceof AbstractSkeletonEntity)
                    weapon = Equip.Type.BOW;
                else if (ent instanceof DrownedEntity)
                    weapon = Equip.Type.TRIDENT;
                else weapon = Equip.Type.SWORD;
                List<Equip.Type> type_list = new ArrayList<>();
                type_list.add(Equip.Type.HELMET);
                type_list.add(Equip.Type.CHESTPLATE);
                type_list.add(Equip.Type.LEGGINGS);
                type_list.add(Equip.Type.BOOTS);
                type_list.add(weapon);
                List<Equip> equips = new ArrayList<>();
                for (Equip equip : ENTRIES) {
                    if (equip.item != null && type_list.contains(equip.type) && equip.cost < max_cost) {
                        equips.add(equip);
                    }
                }
                if (equips.size() == 0)
                    return 0;

                int trial = 1;
                while (equips.size() > 0) {
                    int total_weight = 0;
                    for (Equip equip : equips)
                        total_weight += equip.weight;
                    int rand = world.getRandom().nextInt(total_weight);
                    Equip sele = null;
                    for (Equip equip : equips) {
                        sele = equip;
                        if (rand < equip.weight)
                            break;
                        rand -= equip.weight;
                    }
                    if (max_cost < sele.cost)
                        break;
                    if (sele.chance * trial > world.getRandom().nextDouble()) {
                        max_cost -= sele.cost;
                        items.add(new EnchantPair(new ItemStack(sele.item), sele));
                        Equip select = sele;
                        equips.removeIf(config -> config.type == select.type);
                    }
                }
            }

            // get a list of enchants
            max_cost += ENCHANT * difficulty;
            List<EnchantPair> selected_enc = new ArrayList<>();
            {
                List<EnchantPair> pair = new ArrayList<>();
                for (Equip equip : ENTRIES) {
                    if (equip.enchantment != null && equip.cost < max_cost) {
                        for (EnchantPair is : items)
                            if (equip.enchantment.canEnchant(is.stack))
                                pair.add(new EnchantPair(is.stack, equip));
                    }
                }

                int trial = 1;
                while (pair.size() > 0) {
                    int total_weight = 0;
                    for (EnchantPair enc : pair)
                        total_weight += enc.equip.weight;
                    int rand = world.getRandom().nextInt(total_weight);
                    EnchantPair sele = null;
                    for (EnchantPair enc : pair) {
                        sele = enc;
                        if (rand < enc.equip.weight)
                            break;
                        rand -= enc.equip.weight;
                    }
                    if (max_cost < sele.equip.cost)
                        break;
                    if (sele.equip.chance * trial > world.getRandom().nextDouble()) {
                        max_cost -= sele.equip.cost;
                        selected_enc.add(sele);
                        EnchantPair select = sele;
                        pair.removeIf(config -> config.stack == select.stack);
                    }
                }
            }

            double cost = 0;
            for (EnchantPair is : items) {
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
}
