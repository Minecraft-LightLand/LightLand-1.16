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

    private double accurate_mouse_x, accurate_mouse_y;
    private boolean isScrolling = false;

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
        if (Math.abs(accurate_mouse_x - mx) > 1)
            accurate_mouse_x = mx;
        if (Math.abs(accurate_mouse_y - my) > 1)
            accurate_mouse_y = my;
        //result.render(matrix, this.width / 2, this.height / 2, partial);
        graph.render(matrix, this.width / 2, this.height / 2, accurate_mouse_x, accurate_mouse_y, partial);
    }

    @Override
    public void tick() {
        super.tick();
        result.tick();
    }

    public void mouseMoved(double mx, double my) {
        if (isScrolling)
            return;
        this.accurate_mouse_x = mx;
        this.accurate_mouse_y = my;
    }

    public boolean mouseDragged(double x0, double y0, int button, double dx, double dy) {
        if (button != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else {
                graph.scroll(dx, dy);
            }

            return true;
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (graph.mouseClicked(this.width / 2, this.height / 2, mx, my, button))
            return true;
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double amount) {
        if (graph.mouseScrolled(mx, my, amount))
            return true;
        return super.mouseScrolled(mx, my, amount);
    }

    @Override
    public boolean charTyped(char ch, int type) {
        if (graph.charTyped(ch))
            return true;
        return super.charTyped(ch, type);
    }
}
