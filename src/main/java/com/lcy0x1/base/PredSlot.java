package com.lcy0x1.base;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class PredSlot extends Slot {

    private final Predicate<ItemStack> pred;

    public PredSlot(IInventory inv, int ind, int x, int y, Predicate<ItemStack> pred) {
        super(inv, ind, x, y);
        this.pred = pred;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return pred.test(stack);
    }
}
