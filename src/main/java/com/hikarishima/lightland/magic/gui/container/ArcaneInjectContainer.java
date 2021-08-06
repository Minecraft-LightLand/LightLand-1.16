package com.hikarishima.lightland.magic.gui.container;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.registry.ContainerRegistry;
import com.lcy0x1.core.util.SpriteManager;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArcaneInjectContainer extends AbstractContainer {

    public static final SpriteManager MANAGER = new SpriteManager(LightLand.MODID, "arcane_inject");

    public ArcaneInjectContainer(int wid, PlayerInventory plInv) {
        super(ContainerRegistry.CT_ARCANE_INJECT, wid, plInv, 3, MANAGER);
        //TODO
    }

    @Override
    public void slotsChanged(IInventory inv) {
        super.slotsChanged(inv);
    }

}
