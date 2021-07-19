package com.hikarishima.lightland.registry.item;

import com.hikarishima.lightland.registry.ItemRegistry;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class AntiMagicArmorMaterial implements IArmorMaterial {

    public static final AntiMagicArmorMaterial METAL = new AntiMagicArmorMaterial(
            "anti_magic_metal", 15, new int[]{2, 5, 6, 2}, 0,
            SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F,
            () -> Ingredient.of(ItemRegistry.ANTI_MAGIC_METAL), 0.2f, 0.15f);

    public static final AntiMagicArmorMaterial LIGHT = new AntiMagicArmorMaterial(
            "light_alloy", 37, new int[]{3, 6, 8, 3}, 2,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 2.0F, 0.0F,
            () -> Ingredient.of(ItemRegistry.LIGHT_ALLOY), 0f, 0.15f);

    public static final AntiMagicArmorMaterial ALLOY = new AntiMagicArmorMaterial(
            "anti_magic_alloy", 60, new int[]{3, 6, 8, 3}, 1,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F,
            () -> Ingredient.of(ItemRegistry.ANTI_MAGIC_ALLOY), 0.5f, 0.25f);

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

    private AntiMagicArmorMaterial(
            String name, int durability, int[] defense, int enchant,
            SoundEvent sound, float tough, float res, Supplier<Ingredient> repair,
            float prob, float resist) {
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

    public float getProb(){
        return prob;
    }

    public float getResist(){
        return resist;
    }

}
