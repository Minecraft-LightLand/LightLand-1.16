package com.hikarishima.lightland.magic.registry.item.combat;

import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;

import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
public class MagicItemTier implements IItemTier {

    public static final MagicItemTier METAL = new MagicItemTier(
            2, 250, 6.0F, 2.0F, 0,
            () -> Ingredient.of(MagicItemRegistry.ANTI_MAGIC_METAL.get()), 0.2f, 0.5f);
    public static final MagicItemTier LIGHT = new MagicItemTier(
            4, 2031, 12.0F, 4.0F, 2,
            () -> Ingredient.of(MagicItemRegistry.LIGHT_ALLOY.get()), 0f, 0.5f);
    public static final MagicItemTier ALLOY = new MagicItemTier(
            4, 3000, 9.0F, 4.0F, 1,
            () -> Ingredient.of(MagicItemRegistry.ANTI_MAGIC_ALLOY.get()), 0.5f, 1f);
    public static final MagicItemTier PERMANENCE = new MagicItemTier(
            2, 0, 6f, 2f, 18,
            () -> Ingredient.of(MagicItemRegistry.PERMANENCE_IRON_INGOT.get()), 0, 0);

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final LazyValue<Ingredient> repairIngredient;
    private final float prob, pen;

    private MagicItemTier(int level, int uses, float speed, float damage, int enchant, Supplier<Ingredient> repair, float prob, float pen) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchant;
        this.repairIngredient = new LazyValue<>(repair);
        this.prob = prob;
        this.pen = pen;
    }

    public int getUses() {
        return this.uses;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getAttackDamageBonus() {
        return this.damage;
    }

    public int getLevel() {
        return this.level;
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    public float getProb() {
        return prob;
    }

    public float getPenetrate() {
        return pen;
    }
}
