package com.lcy0x1.base;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class PredSlot extends Slot {

    private final Predicate<ItemStack> pred;
    private BooleanSupplier pickup;

    public PredSlot(IInventory inv, int ind, int x, int y, Predicate<ItemStack> pred) {
        super(inv, ind, x, y);
        this.pred = pred;
    }

    public PredSlot setPickup(BooleanSupplier pickup) {
        this.pickup = pickup;
        return this;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return pred.test(stack);
    }

    @Override
    public boolean mayPickup(PlayerEntity player) {
        return super.mayPickup(player);
    }
}
