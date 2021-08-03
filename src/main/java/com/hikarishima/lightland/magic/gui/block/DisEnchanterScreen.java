package com.hikarishima.lightland.magic.gui.block;

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
public class DisEnchanterScreen extends ContainerScreen<DisEnchanterContainer> {

    public DisEnchanterScreen(DisEnchanterContainer cont, PlayerInventory plInv, ITextComponent title) {
        super(cont, plInv, title);
        this.imageHeight = DisEnchanterContainer.MANAGER.getHeight();
    }

    @Override
    protected void renderBg(MatrixStack matrix, float partial, int mx, int my) {
        SpriteManager sm = DisEnchanterContainer.MANAGER;
        SpriteManager.ScreenRenderer sr = sm.getRenderer(this);
        sr.start(matrix);
        if (!menu.slot.isEmpty())
            sr.draw(matrix, "arrow", sm.within("arrow", mx, my) ? "arrow_2" : "arrow_1");
        if (!menu.map.isEmpty()) {
            int x = 8 + 18 * 3;
            int y = sm.getComp("main_slot").y;
            for (Map.Entry<MagicElement, Integer> ent : menu.map.entrySet()) {
                AbstractHexGui.drawElement(matrix, x, y, ent.getKey(), ent.getValue());
            }
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        SpriteManager sm = DisEnchanterContainer.MANAGER;
        if (!menu.slot.isEmpty() && sm.within("arrow", mx, my)) {
            if (menu.clickMenuButton(Proxy.getClientPlayer(), 0)) {
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
            }
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }
}
