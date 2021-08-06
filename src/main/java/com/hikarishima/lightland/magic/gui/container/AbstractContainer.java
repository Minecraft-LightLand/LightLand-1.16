package com.hikarishima.lightland.magic.gui.container;

import com.lcy0x1.base.PredSlot;
import com.lcy0x1.core.util.SpriteManager;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AbstractContainer extends Container {

    protected final PlayerInventory plInv;
    protected final IInventory slot;

    private final SpriteManager sm;
    private int added = 0;

    protected AbstractContainer(@Nullable ContainerType<?> type, int wid, PlayerInventory plInv, int size, SpriteManager manager) {
        super(type, wid);
        this.plInv = plInv;
        slot = new Inventory(size) {
            @Override
            public void setChanged() {
                super.setChanged();
                slotsChanged(this);
            }
        };
        sm = manager;
        int x = manager.getPlInvX();
        int y = manager.getPlInvY();
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(plInv, j + i * 9 + 9, x + j * 18, y + i * 18));
        for (int k = 0; k < 9; ++k)
            this.addSlot(new Slot(plInv, k, x + k * 18, y + 58));
    }

    protected void addSlot(String name, Predicate<ItemStack> pred) {
        sm.getSlot(name, (x, y) -> new PredSlot(slot, added++, x, y, pred), this::addSlot);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity pl, int id) {
        ItemStack stack = slots.get(id).getItem();
        int n = slot.getContainerSize();
        if (id >= 36) {
            moveItemStackTo(stack, 0, 36, true);
        } else {
            moveItemStackTo(stack, 36, 36 + n, true);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return player.isAlive();
    }

    @Override
    public void removed(PlayerEntity player) {
        clearContainer(player, player.level, slot);
        super.removed(player);
    }
}
