package com.hikarishima.lightland.magic.gui.hex;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;

import java.util.function.IntConsumer;

public class AbstractHexGui extends AbstractGui {

    protected void renderHex(MatrixStack matrix, double x, double y, double r, int color) {
        Matrix4f last = matrix.last().pose();
        BufferBuilder builder = Tessellator.getInstance().getBuilder();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);

        float ca = (float) (color >> 24 & 255) / 255.0F;
        float cr = (float) (color >> 16 & 255) / 255.0F;
        float cg = (float) (color >> 8 & 255) / 255.0F;
        float cb = (float) (color & 255) / 255.0F;
        IntConsumer c = i -> {
            double a = (i + 0.5) * Math.PI / 3;
            float px = (float) (x + r * Math.cos(a));
            float py = (float) (y + r * Math.sin(a));
            builder.vertex(last, px, py, 0).color(cr, cg, cb, ca).endVertex();
        };
        c.accept(0);
        c.accept(3);
        c.accept(2);
        c.accept(1);
        c.accept(0);
        c.accept(5);
        c.accept(4);
        c.accept(3);

        builder.end();
        WorldVertexBufferUploader.end(builder);
    }

    protected void drawIcon(MatrixStack matrix, double x, double y, double scale) {
        RenderSystem.pushMatrix();
        RenderSystem.translated(x, y, 0);
        RenderSystem.scaled(1f / 16 * scale, 1f / 16 * scale, 0);
        this.blit(matrix, -128, -128, 0, 0, 256, 256);
        RenderSystem.popMatrix();
    }

}
