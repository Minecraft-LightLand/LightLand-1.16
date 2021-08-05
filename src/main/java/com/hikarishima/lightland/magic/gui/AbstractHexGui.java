package com.hikarishima.lightland.magic.gui;

import com.hikarishima.lightland.magic.MagicElement;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

import java.util.function.IntConsumer;

public class AbstractHexGui extends AbstractGui {

    public static AbstractHexGui INSTANCE = new AbstractHexGui();

    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");


    public static void renderHex(MatrixStack matrix, double x, double y, double r, int color) {
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

    public static void drawIcon(MatrixStack matrix, double x, double y, double scale) {
        RenderSystem.pushMatrix();
        RenderSystem.translated(x, y, 0);
        RenderSystem.scaled(1f / 16 * scale, 1f / 16 * scale, 0);
        INSTANCE.blit(matrix, -128, -128, 0, 0, 256, 256);
        RenderSystem.popMatrix();
    }

    public static void drawScaled(MatrixStack matrix, double x, double y, int scale) {
        RenderSystem.pushMatrix();
        RenderSystem.translated(x, y, 0);
        RenderSystem.scaled(1f / scale, 1f / scale, 0);
        blit(matrix, -8 * scale, -8 * scale, 0, 0, 16 * scale, 16 * scale, 16 * scale, 16 * scale);
        RenderSystem.popMatrix();
    }

    public static void drawElement(MatrixStack matrix, double x, double y, MagicElement elem, String s) {
        Minecraft.getInstance().getTextureManager().bind(elem.getIcon());
        drawIcon(matrix, x, y, 1);
        FontRenderer font = Minecraft.getInstance().font;
        font.draw(matrix, s, (float) (x + 11 - 1 - font.width(s)), (float) (y + 2), 0x404040);
        font.draw(matrix, s, (float) (x + 11 - 2 - font.width(s)), (float) (y + 1), 0xFFFFFF);
    }

    public static void drawFrame(MatrixStack matrix, FrameType type, boolean unlocked, int x, int y) {
        Minecraft.getInstance().getTextureManager().bind(WIDGETS_LOCATION);
        INSTANCE.blit(matrix, x - 8 - 5, y - 8 - 5, type.getTexture(), 128 + (unlocked ? 0 : 1) * 26, 26, 26);
    }


}
