package com.hikarishima.lightland.magic.gui.container;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.gui.AbstractHexGui;
import com.hikarishima.lightland.proxy.Proxy;
import com.lcy0x1.core.util.SpriteManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public class MagicCraftScreen extends AbstractScreen<MagicCraftContainer> {

    public MagicCraftScreen(MagicCraftContainer cont, PlayerInventory plInv, ITextComponent title) {
        super(cont, plInv, title);
        this.imageHeight = MagicCraftContainer.MANAGER.getHeight();
        this.inventoryLabelY = MagicCraftContainer.MANAGER.getPlInvY() - 11;
    }

    @Override
    protected void renderBg(MatrixStack matrix, float partial, int mx, int my) {
        mx -= getGuiLeft();
        my -= getGuiTop();
        SpriteManager sm = MagicCraftContainer.MANAGER;
        SpriteManager.ScreenRenderer sr = sm.getRenderer(this);
        sr.start(matrix);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        SpriteManager sm = MagicCraftContainer.MANAGER;
        if (!menu.slot.isEmpty() && sm.within("arrow", mx - getGuiLeft(), my - getGuiTop())) {
            if (menu.clickMenuButton(Proxy.getClientPlayer(), 0)) {
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
            }
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

}
