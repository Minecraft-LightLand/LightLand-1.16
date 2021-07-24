package com.hikarishima.lightland.magic.gui.hex;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MagicHexScreen extends Screen {

    private static final ITextComponent TITLE = new TranslationTextComponent("gui.advancements");

    public final MagicHandler handler;
    public final MagicProduct<?, ?> product;
    public final HexGraphGui graph;
    public final HexResultGui result;

    public MagicHexScreen(MagicHandler handler, MagicProduct<?, ?> product) {
        super(TITLE);
        this.handler = handler;
        this.product = product;
        this.graph = new HexGraphGui(this);
        this.result = new HexResultGui(this);
    }

    @Override
    public void render(MatrixStack matrix, int mx, int my, float partial) {
        super.renderBackground(matrix);
        super.render(matrix, 0, 0, partial);
        result.render(matrix, this.width / 2, this.height / 2, partial);
    }

    @Override
    public void tick() {
        super.tick();
        result.tick();
    }
}
