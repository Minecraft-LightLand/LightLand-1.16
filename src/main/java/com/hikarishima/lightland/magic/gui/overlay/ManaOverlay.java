package com.hikarishima.lightland.magic.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;

public class ManaOverlay extends AbstractGui {

    public static ManaOverlay INSTANCE = new ManaOverlay();

    public void render(MatrixStack matrix, MainWindow window, float partial) {
        FontRenderer font = Minecraft.getInstance().font;
        int width = window.getWidth();
        int height = window.getHeight();
        int bar_width = width / 2 - 91;
        int bar_h = height - 29;
        int k = 182;
        blit(matrix, bar_width, bar_h, 0, 64, 182, 5);
        blit(matrix, bar_width, bar_h, 0, 69, k, 5);
        String s = "text";
        int x = (width - font.width(s)) / 2;
        int y = height - 35;
        int col = 0x80FF20;
        font.draw(matrix, s, x + 1, y, 0);
        font.draw(matrix, s, x - 1, y, 0);
        font.draw(matrix, s, x, y + 1, 0);
        font.draw(matrix, s, x, y - 1, 0);
        font.draw(matrix, s, x, y, col);
        //TODO
    }

}
