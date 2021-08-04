package com.hikarishima.lightland.magic.gui.overlay;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.proxy.Proxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;

public abstract class AbstractOverlay extends AbstractGui {

    protected MatrixStack matrix;
    protected int width, height;
    protected ClientPlayerEntity player;
    protected MagicHandler handler;
    protected TextureManager tm;
    protected FontRenderer font;

    public final boolean render(MatrixStack matrix, MainWindow window, float partial) {
        this.matrix = matrix;
        player = Proxy.getClientPlayer();
        handler = player == null ? null : MagicHandler.get(player);
        width = window.getGuiScaledWidth();
        height = window.getGuiScaledHeight();
        font = Minecraft.getInstance().font;
        tm = Minecraft.getInstance().getTextureManager();
        return render();
    }

    protected abstract boolean render();

}
