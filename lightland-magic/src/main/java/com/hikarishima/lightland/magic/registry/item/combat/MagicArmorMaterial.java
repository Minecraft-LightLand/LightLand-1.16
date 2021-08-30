package com.hikarishima.lightland.magic.registry.item.combat;

import com.hikarishima.lightland.magic.capabilities.weight.IWeightedMaterial;
import com.hikarishima.lightland.magic.capabilities.weight.WeightCalculator;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
public class MagicArmorMaterial implements IArmorMaterial, IWeightedMaterial {

    public static final MagicArmorMaterial METAL = new MagicArmorMaterial(
            "anti_magic_metal", 15, new int[]{2, 5, 6, 2}, 0,
            SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F,
            () -> Ingredient.of(MagicItemRegistry.ANTI_MAGIC_METAL), 0.2f, 0.12f,
            WeightCalculator.getWeight(70, 0));

    public static final MagicArmorMaterial LIGHT = new MagicArmorMaterial(
            "light_alloy", 37, new int[]{3, 6, 8, 3}, 2,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 2.0F, 0.0F,
            () -> Ingredient.of(MagicItemRegistry.LIGHT_ALLOY), 0f, 0.15f,
            WeightCalculator.getWeight(70, 0));

    public static final MagicArmorMaterial ALLOY = new MagicArmorMaterial(
            "anti_magic_alloy", 60, new int[]{3, 6, 8, 3}, 1,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F,
            () -> Ingredient.of(MagicItemRegistry.ANTI_MAGIC_ALLOY), 0.5f, 0.25f,
            WeightCalculator.getWeight(70, 0));

    public static final MagicArmorMaterial CLOTH = new MagicArmorMaterial(
            "enchant_cloth", 15, new int[]{1, 3, 2, 1}, 40,
            SoundEvents.ARMOR_EQUIP_LEATHER, 10.0F, 0.0F,
            () -> Ingredient.of(MagicItemRegistry.ENCHANT_CLOTH), 0, 0.15f,
            WeightCalculator.getWeight(20, 0));

    public static final MagicArmorMaterial CHAIN = new MagicArmorMaterial(
            "enchant_chain", 30, new int[]{2, 5, 6, 2}, 60,
            SoundEvents.ARMOR_EQUIP_CHAIN, 20.0F, 0.0F,
            () -> Ingredient.of(MagicItemRegistry.ENCHANT_CHAIN), 0, 0.20f,
            WeightCalculator.getWeight(25, 0));

    public static final MagicArmorMaterial ENCH_LIGHT = new MagicArmorMaterial(
            "enchant_light", 60, new int[]{3, 6, 8, 3}, 60,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 30.0F, 0.0F,
            () -> Ingredient.of(MagicItemRegistry.ENCHANT_LIGHT_INGOT), 0, 0.25f,
            WeightCalculator.getWeight(25, 70));

    public static final MagicArmorMaterial PERMANENCE = new MagicArmorMaterial(
            "permanence", 0, new int[]{2, 5, 6, 2}, 18,
            SoundEvents.ARMOR_EQUIP_IRON, 0f, 0f,
            () -> Ingredient.of(MagicItemRegistry.PERMANENCE_IRON_INGOT), 0, 0,
            WeightCalculator.getWeight(70, 0));

    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] slotProtections;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyValue<Ingredient> repairIngredient;
    private final float prob, resist;
    private final int[] slot_weight;

    private MagicArmorMaterial(
            String name, int durability, int[] defense, int enchant,
            SoundEvent sound, float tough, float res, Supplier<Ingredient> repair,
            float prob, float resist, int[] slot_weight) {
        this.name = name;
        this.durabilityMultiplier = durability;
        this.slotProtections = defense;
        this.enchantmentValue = enchant;
        this.sound = sound;
        this.toughness = tough;
        this.knockbackResistance = res;
        this.repairIngredient = new LazyValue<>(repair);
        this.prob = prob;
        this.resist = resist;
        this.slot_weight = slot_weight;
    }

    public int getDurabilityForSlot(EquipmentSlotType slot) {
        return HEALTH_PER_SLOT[slot.getIndex()] * this.durabilityMultiplier;
    }

    public int getDefenseForSlot(EquipmentSlotType slot) {
        return this.slotProtections[slot.getIndex()];
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public SoundEvent getEquipSound() {
        return this.sound;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @OnlyIn(Dist.CLIENT)
    public String getName() {
        return this.name;
    }

    public float getToughness() {
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    public float getProb() {
        return prob;
    }

    public float getResist() {
        return resist;
    }

    @Override
    public int getWeight(EquipmentSlotType slot) {
        return this.slot_weight[slot.getIndex()];
    }

}
