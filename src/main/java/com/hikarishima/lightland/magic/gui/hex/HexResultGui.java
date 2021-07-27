package com.hikarishima.lightland.magic.gui.hex;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class HexResultGui extends AbstractGui {

    private static final int FLOW_COUNT = 5;
    private static final float RADIUS = 30, PERIOD = 60, SCALE_NODE = 1f, SCALE_FLOW = 0.5f;

    private final Minecraft minecraft = Minecraft.getInstance();
    private final MagicHexScreen screen;
    private final IMagicRecipe<?> recipe;

    private int tick = 0;

    final WindowBox box = new WindowBox();

    public HexResultGui(MagicHexScreen screen) {
        this.screen = screen;
        this.recipe = screen.product.recipe;
    }

    public void render(MatrixStack matrix, float partial) {
        float x0 = box.x + box.w / 2f;
        float y0 = box.y + box.h / 2f;
        float progress = (tick + partial) / PERIOD % 1;
        MagicElement[] elements = recipe.getElements();
        boolean[][] graph = recipe.getGraph();
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x0, y0, 0);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        for (int i = 0; i < elements.length; i++) {
            double ri = i * Math.PI / 3;
            double xi = RADIUS * Math.cos(ri);
            double yi = RADIUS * Math.sin(ri);
            minecraft.getTextureManager().bind(elements[i].getIcon());
            for (int j = 0; j < elements.length; j++) {
                if (!graph[i][j])
                    continue;
                double rj = j * Math.PI / 3;
                double xj = RADIUS * Math.cos(rj);
                double yj = RADIUS * Math.sin(rj);
                for (int k = 0; k < FLOW_COUNT; k++) {
                    double p = (progress + k * 1.0 / FLOW_COUNT) % 1;
                    double xp = xi + (xj - xi) * p;
                    double yp = yi + (yj - yi) * p;
                    drawIcon(matrix, xp, yp, SCALE_FLOW);
                }
            }
        }
        RenderSystem.disableBlend();
        for (int i = 0; i < elements.length; i++) {
            minecraft.getTextureManager().bind(elements[i].getIcon());
            double ri = i * Math.PI / 3;
            double xi = RADIUS * Math.cos(ri);
            double yi = RADIUS * Math.sin(ri);
            drawIcon(matrix, xi, yi, SCALE_NODE);
        }
        RenderSystem.popMatrix();
    }

    private void drawIcon(MatrixStack matrix, double x, double y, double scale) {
        RenderSystem.pushMatrix();
        RenderSystem.translated(x, y, 0);
        RenderSystem.scaled(1f / 16 * scale, 1f / 16 * scale, 0);
        this.blit(matrix, -128, -128, 0, 0, 256, 256);
        RenderSystem.popMatrix();
    }

    public void tick() {
        tick++;
        tick %= PERIOD;
    }

    public boolean mouseDragged(double x0, double y0, int button, double dx, double dy) {

        return false;
    }
}
