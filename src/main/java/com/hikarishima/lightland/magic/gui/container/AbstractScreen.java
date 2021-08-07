package com.hikarishima.lightland.magic.gui.container;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractScreen<T extends AbstractContainer> extends ContainerScreen<T> {

    public AbstractScreen(T cont, PlayerInventory plInv, ITextComponent title) {
        super(cont, plInv, title);
        this.imageHeight = menu.sm.getHeight();
        this.inventoryLabelY = menu.sm.getPlInvY() - 11;
    }

    @Override
    public void render(MatrixStack stack, int mx, int my, float partial) {
        super.render(stack, mx, my, partial);
        renderTooltip(stack, mx, my);
    }
}
