package com.hikarishima.lightland.magic.gui.overlay;

import com.hikarishima.lightland.proxy.Proxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;

public class ManaOverlay extends AbstractGui {

    public static ManaOverlay INSTANCE = new ManaOverlay();

    public void render(MatrixStack matrix, MainWindow window, float partial) {
        Minecraft m = Minecraft.getInstance();
        PlayerEntity pl = Proxy.getPlayer();
        int width = window.getWidth();
        int height = window.getHeight();
        int x0 = width / 2 - 91;
        int dy = 8;
        int y0 = height - 29 + dy;
        renderBar(matrix, x0, y0 -= dy, pl.experienceProgress, "" + pl.experienceLevel, 0, 0x80FF20);
        renderBar(matrix, x0, y0 -= dy, pl.experienceProgress, "" + pl.experienceLevel, 0, 0x80FF20);
        renderBar(matrix, x0, y0 -= dy, pl.experienceProgress, "" + pl.experienceLevel, 0, 0x80FF20);
        renderBar(matrix, x0, y0 -= dy, pl.experienceProgress, "" + pl.experienceLevel, 0, 0x80FF20);
    }

    private void renderBar(MatrixStack matrix, int x0, int y0, float f, String s, int bg, int col) {
        FontRenderer font = Minecraft.getInstance().font;
        int w = 182;
        int k = (int) (f * (w + 1));
        blit(matrix, x0, y0, 0, 64, w, 5);
        blit(matrix, x0, y0, 0, 69, k, 5);
        int x = x0 + (w - font.width(s)) / 2;
        int y = y0 - 6;
        font.draw(matrix, s, x + 1, y, bg);
        font.draw(matrix, s, x - 1, y, bg);
        font.draw(matrix, s, x, y + 1, bg);
        font.draw(matrix, s, x, y - 1, bg);
        font.draw(matrix, s, x, y, col);
    }

}
