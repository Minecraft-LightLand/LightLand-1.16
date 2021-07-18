package com.hikarishima.lightland.registry.item;

import com.hikarishima.lightland.magic.arcane.internal.ArcaneItemUseHelper;
import com.hikarishima.lightland.magic.arcane.internal.IArcaneItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public class ArcaneSword extends SwordItem implements IArcaneItem {

    public ArcaneSword(IItemTier tier, int attack, float speed, Properties props) {
        super(tier, attack, speed, props.durability(10));
    }

    @ParametersAreNonnullByDefault
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity user) {
        return true;
    }

    @ParametersAreNonnullByDefault
    public boolean mineBlock(ItemStack stack, World w, BlockState state, BlockPos pos, LivingEntity user) {
        return true;
    }

    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    public double getDurabilityForDisplay(ItemStack stack) {
        return 1 - 1.0 * ArcaneItemUseHelper.getArcaneMana(stack) / getMaxMana(stack);
    }

    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return 0xFFFFFF;
    }

    @Override
    public int getMaxMana(ItemStack stack) {
        return 10;
    }

}
