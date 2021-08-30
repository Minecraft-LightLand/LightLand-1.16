package com.hikarishima.lightland.magic.registry.block;

import com.lcy0x1.base.BaseTileEntity;
import com.lcy0x1.core.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SerialClass
public abstract class SyncedSingleItemTE extends BaseTileEntity implements ISidedInventory {

    private static final int[] SLOTS = {0};

    @SerialClass.SerialField(toClient = true)
    public ItemStack stack = ItemStack.EMPTY;

    public SyncedSingleItemTE(TileEntityType<?> type) {
        super(type);
    }

    public boolean isLocked() {
        return false;
    }

    @Override
    public int[] getSlotsForFace(Direction dire) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dire) {
        return !isLocked() && slot == 0;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dire) {
        return !isLocked() && slot == 0;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? stack : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack ans = stack;
        stack = ItemStack.EMPTY;
        setChanged();
        return ans;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack ans = stack;
        stack = ItemStack.EMPTY;
        return ans;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            if (stack.isEmpty()) {
                this.stack = ItemStack.EMPTY;
            } else {
                this.stack = stack.split(1);
            }
            setChanged();
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public void clearContent() {
        stack = ItemStack.EMPTY;
        setChanged();
    }

}

