package com.hikarishima.lightland.registry.item;

import com.hikarishima.lightland.magic.arcane.internal.ArcaneItemUseHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public class ArcaneAxe extends AxeItem {

    public ArcaneAxe(IItemTier tier, float attack, float speed, Properties props) {
        super(tier, attack, speed, props.defaultDurability(10));
    }

    @ParametersAreNonnullByDefault
    public boolean isFoil(ItemStack stack) {
        return ArcaneItemUseHelper.isAxeCharged(stack);
    }

    @ParametersAreNonnullByDefault
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity user) {
        return true;
    }

    @ParametersAreNonnullByDefault
    public boolean mineBlock(ItemStack stack, World w, BlockState state, BlockPos pos, LivingEntity user) {
        return true;
    }

}
