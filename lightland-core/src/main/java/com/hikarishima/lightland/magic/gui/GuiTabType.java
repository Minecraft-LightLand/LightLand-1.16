package com.hikarishima.lightland.magic.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public enum GuiTabType {
    ABOVE(0, 0, 28, 32, 8),
    BELOW(84, 0, 28, 32, 8),
    LEFT(0, 64, 32, 28, 5),
    RIGHT(96, 64, 32, 28, 5);

    public static final int MAX_TABS = Arrays.stream(values()).mapToInt((tab) -> tab.max).sum();
    private final int textureX;
    private final int textureY;
    private final int width;
    private final int height;
    private final int max;

    GuiTabType(int tx, int ty, int w, int h, int max) {
        this.textureX = tx;
        this.textureY = ty;
        this.width = w;
        this.height = h;
        this.max = max;
    }

    public int getMax() {
        return this.max;
    }

    public void draw(MatrixStack matrix, AbstractGui gui, int x0, int y0, boolean selected, int index) {
        int i = this.textureX;
        if (index > 0) {
            i += this.width;
        }

        if (index == this.max - 1) {
            i += this.width;
        }

        int j = selected ? this.textureY + this.height : this.textureY;
        gui.blit(matrix, x0 + this.getX(index), y0 + this.getY(index), i, j, this.width, this.height);
    }

    public void drawIcon(int x0, int y0, int index, ItemRenderer renderer, ItemStack stack) {
        int i = x0 + this.getX(index);
        int j = y0 + this.getY(index);
        switch (this) {
            case ABOVE:
                i += 6;
                j += 9;
                break;
            case BELOW:
                i += 6;
                j += 6;
                break;
            case LEFT:
                i += 10;
                j += 5;
                break;
            case RIGHT:
                i += 6;
                j += 5;
        }

        renderer.renderAndDecorateFakeItem(stack, i, j);
    }

    public int getX(int index) {
        switch (this) {
            case ABOVE:
            case BELOW:
                return (this.width + 4) * index;
            case LEFT:
                return -this.width + 4;
            case RIGHT:
                return 248;
            default:
                throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
        }
    }

    public int getY(int index) {
        switch (this) {
            case ABOVE:
                return -this.height + 4;
            case BELOW:
                return 136;
            case LEFT:
            case RIGHT:
                return this.height * index;
            default:
                throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
        }
    }

    public boolean isMouseOver(int x0, int y0, int index, double mx, double my) {
        int i = x0 + this.getX(index);
        int j = y0 + this.getY(index);
        return mx > (double) i && mx < (double) (i + this.width) && my > (double) j && my < (double) (j + this.height);
    }
}
