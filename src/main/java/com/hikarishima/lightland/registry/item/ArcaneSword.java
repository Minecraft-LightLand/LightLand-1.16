package com.hikarishima.lightland.registry.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public class ArcaneSword extends SwordItem {

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

}
