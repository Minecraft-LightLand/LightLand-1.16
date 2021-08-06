package com.hikarishima.lightland.magic.gui.container;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.registry.ContainerRegistry;
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
public class MagicCraftContainer extends AbstractContainer {

    public static final SpriteManager MANAGER = new SpriteManager(LightLand.MODID, "magic_craft");

    public MagicCraftContainer(int wid, PlayerInventory plInv) {
        super(ContainerRegistry.CT_MAGIC_CRAFT, wid, plInv, 3, MANAGER);
    }

    @Override
    public void slotsChanged(IInventory inv) {
        super.slotsChanged(inv);
    }

}
