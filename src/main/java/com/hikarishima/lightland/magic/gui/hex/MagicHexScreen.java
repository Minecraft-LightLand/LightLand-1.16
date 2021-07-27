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

    public void init() {
        int sw = this.width;
        int sh = this.height;
        int w = 300;
        int h = 200;
        int x0 = (sw - w) / 2;
        int y0 = (sh - h) / 2;
        graph.box.setSize(this, x0, y0, 200, 200, 8);
        result.box.setSize(this, x0 + 200, y0, 100, 100, 8);
    }

    @Override
    public void render(MatrixStack matrix, int mx, int my, float partial) {
        int col_bg = 0xFFC0C0C0;
        int col_m0 = 0xFF808080;
        int col_m1 = 0xFFFFFFFF;
        super.renderBackground(matrix);
        super.render(matrix, 0, 0, partial);
        if (Math.abs(accurate_mouse_x - mx) > 1)
            accurate_mouse_x = mx;
        if (Math.abs(accurate_mouse_y - my) > 1)
            accurate_mouse_y = my;
        //graph.box.render(matrix, 0, col_bg, WindowBox.RenderType.FILL);
        graph.render(matrix, accurate_mouse_x, accurate_mouse_y, partial);
        //graph.box.render(matrix, 0, col_bg, WindowBox.RenderType.MARGIN_ALL);
        graph.box.render(matrix, 8, col_m1, WindowBox.RenderType.MARGIN);
        graph.box.render(matrix, 2, col_m0, WindowBox.RenderType.MARGIN);

        result.box.render(matrix, 0, col_bg, WindowBox.RenderType.FILL);
        result.render(matrix, partial);
        result.box.render(matrix, 8, col_m1, WindowBox.RenderType.MARGIN);
        result.box.render(matrix, 2, col_m0, WindowBox.RenderType.MARGIN);
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
            isScrolling = false;
            return false;
        } else {
            if (graph.box.isMouseIn(x0, y0)) {
                isScrolling = true;
                graph.scroll(dx, dy);
                return true;
            } else if (result.box.isMouseIn(x0, y0)) {
                return result.mouseDragged(x0, y0, button, dx, dy);
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (graph.box.isMouseIn(mx, my) && graph.mouseClicked(mx, my, button))
            return true;
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double amount) {
        if (graph.box.isMouseIn(mx, my) && graph.mouseScrolled(mx, my, amount))
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
