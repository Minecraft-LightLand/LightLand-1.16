package com.hikarishima.lightland.magic.gui.container;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.products.instance.SpellMagic;
import com.hikarishima.lightland.registry.ContainerRegistry;
import com.hikarishima.lightland.registry.ItemRegistry;
import com.hikarishima.lightland.registry.item.magic.MagicScroll;
import com.hikarishima.lightland.registry.item.magic.MagicWand;
import com.lcy0x1.base.PredSlot;
import com.lcy0x1.core.util.SpriteManager;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SpellCraftContainer extends Container {

    public static final SpriteManager MANAGER = new SpriteManager(LightLand.MODID, "spell_craft");

    protected final PlayerInventory plInv;
    protected final IInventory slot = new Inventory(4) {
        @Override
        public void setChanged() {
            super.setChanged();
            slotsChanged(this);
        }
    };

    public SpellCraftContainer(int wid, PlayerInventory plInv) {
        super(ContainerRegistry.CT_SPELL_CRAFT, wid);
        this.plInv = plInv;
        MANAGER.getSlot("wand_slot", (x, y) -> new PredSlot(slot, 0, x, y,
                stack -> stack.getItem() instanceof MagicWand && ((MagicWand) stack.getItem())
                        .getData(plInv.player, stack) instanceof SpellMagic), this::addSlot);
        MANAGER.getSlot("input_slot", (x, y) -> new PredSlot(slot, 1, x, y,
                stack -> stack.getItem() instanceof MagicScroll), this::addSlot);
        MANAGER.getSlot("input_slot", (x, y) -> new PredSlot(slot, 2, x, y,
                stack -> stack.getItem() == ItemRegistry.ENCHANT_GOLD_NUGGET), this::addSlot);
        MANAGER.getSlot("input_slot", (x, y) -> new PredSlot(slot, 3, x, y,
                stack -> false), this::addSlot);
        int x = MANAGER.getPlInvX();
        int y = MANAGER.getPlInvY();
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(plInv, j + i * 9 + 9, x + j * 18, y + i * 18));
        for (int k = 0; k < 9; ++k)
            this.addSlot(new Slot(plInv, k, x + k * 18, y + 58));
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return player.isAlive();
    }

    @Override
    public void slotsChanged(IInventory inv) {
        super.slotsChanged(inv);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity pl, int id) {
        return ItemStack.EMPTY;
    }
}
