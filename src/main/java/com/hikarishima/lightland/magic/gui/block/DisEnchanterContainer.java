package com.hikarishima.lightland.magic.gui.block;

import com.hikarishima.lightland.registry.ContainerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DisEnchanterContainer extends Container {

    private final IInventory slot = new Inventory(1) {
        public void setChanged() {
            super.setChanged();
            DisEnchanterContainer.this.slotsChanged(this);
        }
    };

    public DisEnchanterContainer(int wid, PlayerInventory plInv) {
        super(ContainerRegistry.CT_DISENCH, wid);
        this.addSlot(new Slot(slot, 0, 0, 0) {
            public boolean mayPlace(ItemStack stack) {
                return stack.isEnchanted();
            }

            public int getMaxStackSize() {
                return 1;
            }
        });

        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(plInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for (int k = 0; k < 9; ++k)
            this.addSlot(new Slot(plInv, k, 8 + k * 18, 142));

    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return player.isAlive();
    }

    public void slotsChanged(IInventory inv) {

    }

}
