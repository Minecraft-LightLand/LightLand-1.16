package com.hikarishima.lightland.registry.item.magic;

import com.hikarishima.lightland.magic.arcane.internal.ArcaneItemUseHelper;
import com.hikarishima.lightland.magic.arcane.internal.IArcaneItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public class ArcaneAxe extends AxeItem implements IArcaneItem {

    private final int mana;

    public ArcaneAxe(IItemTier tier, float attack, float speed, Properties props, int mana) {
        super(tier, attack, speed, props);
        this.mana = mana;
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
        return mana;
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return ActionResult.success(stack);
    }

}
