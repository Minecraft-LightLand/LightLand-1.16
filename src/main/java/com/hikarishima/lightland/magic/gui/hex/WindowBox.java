package com.hikarishima.lightland.magic.gui.hex;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;

public class WindowBox extends AbstractGui {

    private Screen parent;
    public int x, y, w, h;

    public void setSize(Screen parent, int x, int y, int w, int h, int sh) {
        this.x = x + sh;
        this.y = y + sh;
        this.w = w - sh * 2;
        this.h = h - sh * 2;
        this.parent = parent;
    }

    public boolean isMouseIn(double mx, double my) {
        return mx > x && mx < x + w && my > y && my < y + h;
    }

    public void render(MatrixStack matrix, int sh, int color, RenderType type) {
        if (type == RenderType.FILL) {
            fill(matrix, x, y, x + w, y + h, color);
        } else if (type == RenderType.MARGIN) {
            fill(matrix, x - sh, y - sh, x + w + sh, y, color);
            fill(matrix, x - sh, y + h, x + w + sh, y + h + sh, color);
            fill(matrix, x - sh, y, x, y + h, color);
            fill(matrix, x + w, y, x + w + sh, y + h, color);
        } else {
            fill(matrix, 0, 0, parent.width, y, color);
            fill(matrix, 0, y + h, parent.width, parent.height, color);
            fill(matrix, 0, y, x, y + h, color);
            fill(matrix, x + w, y, parent.width, y + h, color);
        }
    }

    public void startClip(MatrixStack matrix) {
        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrix, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.depthFunc(518);
        fill(matrix, x, y, x + w, y + h, -16777216);
        RenderSystem.depthFunc(515);
    }

    public void endClip(MatrixStack matrix) {
        RenderSystem.depthFunc(518);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrix, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.depthFunc(515);
        RenderSystem.popMatrix();
    }

    public enum RenderType {
        MARGIN, FILL, MARGIN_ALL
    }

}
