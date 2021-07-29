package com.hikarishima.lightland.magic.gui.magic_tree;

import com.google.common.collect.Lists;
import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.products.info.DisplayInfo;
import com.hikarishima.lightland.magic.products.info.ProductState;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;

public class MagicTreeEntry<I extends IForgeRegistryEntry<I>, P extends MagicProduct<I, P>> extends AbstractGui {

    private static final float X_SLOT = 28, Y_SLOT = 27;
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};

    private final Minecraft minecraft = Minecraft.getInstance();
    private final List<IReorderingProcessor> description;
    private final DisplayInfo display;
    private final P product;
    private final MagicTreeGui<I, P> tab;
    private final IReorderingProcessor title;
    private final int x, y, width;

    protected List<MagicTreeEntry<I, P>> parents = Lists.newArrayList();

    public MagicTreeEntry(MagicTreeGui<I, P> tab, P product, DisplayInfo display) {
        this.tab = tab;
        this.product = product;
        this.display = display;
        this.title = LanguageMap.getInstance().getVisualOrder(minecraft.font.substrByWidth(Translator.get(product), 163));
        this.x = Math.round(display.getX() * X_SLOT);
        this.y = Math.round(display.getY() * Y_SLOT);
        int title_width = Math.max(80, 29 + minecraft.font.width(this.title));
        this.description = LanguageMap.getInstance().getVisualOrder(
                findOptimalLines(TextComponentUtils.mergeStyles(Translator.getDesc(product).copy(),
                        Style.EMPTY.withColor(display.getFrame().getChatColor())), title_width));
        for (IReorderingProcessor text : this.description) {
            title_width = Math.max(title_width, minecraft.font.width(text));
        }
        this.width = title_width + 8;
    }

    private static float getMaxWidth(CharacterManager splitter, List<ITextProperties> list) {
        return (float) list.stream().mapToDouble(splitter::stringWidth).max().orElse(0.0D);
    }

    public void drawConnectivity(MatrixStack matrix, int x0, int y0, boolean shadow) {
        for (MagicTreeEntry<I, P> parent : parents) {
            int px = x0 + parent.x + 13;
            int py = y0 + parent.y + 13;
            int cx = x0 + this.x + 13;
            int cy = y0 + this.y + 13;
            int color = shadow ? -16777216 : -1;
            connect(matrix, cx, cy, px, py, color, shadow);
        }
    }

    private void connect(MatrixStack matrix, int x0, int y0, int x1, int y1, int color, boolean shadow) {
        if (y0 == y1) {
            int min = Math.min(x0, x1);
            int max = Math.max(x0, x1);
            if (shadow) {
                this.hLine(matrix, min, max, y0 - 1, color);
                this.hLine(matrix, min + 1, max, y0, color);
                this.hLine(matrix, min, max, y0 + 1, color);
            } else {
                this.hLine(matrix, min, max, y0, color);
            }
        } else {
            int min = Math.min(y0, y1);
            int max = Math.max(y0, y1);
            if (shadow) {
                this.vLine(matrix, x0 - 1, min, max, color);
                this.vLine(matrix, x0 + 1, min, max, color);
            } else {
                this.vLine(matrix, x0, min, max, color);
            }
        }
    }

    public void draw(MatrixStack matrix, int x0, int y0) {
        ProductState state = product.getState();
        int icon_index = state == ProductState.LOCKED ? 1 : 0;
        if (product.visible()) {
            this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
            this.blit(matrix, x0 + this.x + 3, y0 + this.y, this.display.getFrame().getTexture(), 128 + icon_index * 26, 26, 26);
            this.minecraft.getItemRenderer().renderAndDecorateFakeItem(this.display.getIcon(), x0 + this.x + 8, y0 + this.y + 5);
        }
    }

    public boolean isMouseOver(int x0, int y0, int mx, int my) {
        if (product.visible()) {
            int lvt_5_1_ = x0 + this.x;
            int lvt_6_1_ = lvt_5_1_ + 26;
            int lvt_7_1_ = y0 + this.y;
            int lvt_8_1_ = lvt_7_1_ + 26;
            return mx >= lvt_5_1_ && mx <= lvt_6_1_ && my >= lvt_7_1_ && my <= lvt_8_1_;
        } else {
            return false;
        }
    }

    public void drawHover(MatrixStack matrix, int sx, int sy, float fade, int x0, int y0) {
        ProductState state = product.getState();
        boolean exceed_width = x0 + sx + this.x + this.width + 26 >= this.tab.getScreen().width;
        int height = 113 - sy - this.y - 26;
        int desc_size = this.description.size();
        boolean lvt_10_1_ = height <= 6 + desc_size * 9;
        int state_index = 1;
        int icon_index = state == ProductState.LOCKED ? 1 : 0;

        int lvt_16_1_ = this.width - this.width / 2;
        minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        int lvt_17_1_ = sy + this.y;
        int lvt_18_2_;
        if (exceed_width) {
            lvt_18_2_ = sx + this.x - this.width + 26 + 6;
        } else {
            lvt_18_2_ = sx + this.x;
        }

        int var10001 = this.description.size();
        int lvt_19_1_ = 32 + var10001 * 9;
        if (!this.description.isEmpty()) {
            if (lvt_10_1_) {
                this.render9Sprite(matrix, lvt_18_2_, lvt_17_1_ + 26 - lvt_19_1_, this.width, lvt_19_1_, 10, 200, 26, 0, 52);
            } else {
                this.render9Sprite(matrix, lvt_18_2_, lvt_17_1_, this.width, lvt_19_1_, 10, 200, 26, 0, 52);
            }
        }

        this.blit(matrix, lvt_18_2_, lvt_17_1_, 0, state_index * 26, this.width / 2, 26);
        this.blit(matrix, lvt_18_2_ + this.width / 2, lvt_17_1_, 200 - lvt_16_1_, state_index * 26, lvt_16_1_, 26);

        // icon
        this.blit(matrix, sx + this.x + 3, sy + this.y, this.display.getFrame().getTexture(), 128 + icon_index * 26, 26, 26);

        minecraft.font.drawShadow(matrix, this.title, (float) (exceed_width ? lvt_18_2_ + 5 : sx + this.x + 32), (float) (sy + this.y + 9), -1);

        FontRenderer fontRenderer = minecraft.font;
        for (int i = 0; i < this.description.size(); ++i) {
            IReorderingProcessor text = this.description.get(i);
            float x = (float) (lvt_18_2_ + 5);
            int y = lvt_10_1_ ? lvt_17_1_ + 26 - lvt_19_1_ + 7 : sy + this.y + 9 + 17;
            fontRenderer.draw(matrix, text, x, (float) (y + i * 9), -5592406);
        }

        minecraft.getItemRenderer().renderAndDecorateFakeItem(this.display.getIcon(), sx + this.x + 8, sy + this.y + 5);
    }

    private void render9Sprite(MatrixStack matrix, int x0, int y0, int x1, int y1, int px, int dx, int dy, int tx, int ty) {
        this.blit(matrix, x0, y0, tx, ty, px, px);
        this.renderRepeating(matrix, x0 + px, y0, x1 - px - px, px, tx + px, ty, dx - px - px, dy);
        this.blit(matrix, x0 + x1 - px, y0, tx + dx - px, ty, px, px);
        this.blit(matrix, x0, y0 + y1 - px, tx, ty + dy - px, px, px);
        this.renderRepeating(matrix, x0 + px, y0 + y1 - px, x1 - px - px, px, tx + px, ty + dy - px, dx - px - px, dy);
        this.blit(matrix, x0 + x1 - px, y0 + y1 - px, tx + dx - px, ty + dy - px, px, px);
        this.renderRepeating(matrix, x0, y0 + px, px, y1 - px - px, tx, ty + px, dx, dy - px - px);
        this.renderRepeating(matrix, x0 + px, y0 + px, x1 - px - px, y1 - px - px, tx + px, ty + px, dx - px - px, dy - px - px);
        this.renderRepeating(matrix, x0 + x1 - px, y0 + px, px, y1 - px - px, tx + dx - px, ty + px, dx, dy - px - px);
    }

    private void renderRepeating(MatrixStack matrix, int x0, int y0, int nx, int ny, int tx, int ty, int dx, int dy) {
        for (int ix = 0; ix < nx; ix += dx) {
            int x = x0 + ix;
            int mx = Math.min(dx, nx - ix);
            for (int iy = 0; iy < ny; iy += dy) {
                int y = y0 + iy;
                int my = Math.min(dy, ny - iy);
                this.blit(matrix, x, y, tx, ty, mx, my);
            }
        }

    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    private List<ITextProperties> findOptimalLines(ITextComponent text, int width) {
        CharacterManager splitter = this.minecraft.font.getSplitter();
        List<ITextProperties> list = null;
        float max = 3.4028235E38F;
        for (int offset : TEST_SPLIT_OFFSETS) {
            List<ITextProperties> splitLines = splitter.splitLines(text, width - offset, Style.EMPTY);
            float maxWidth = Math.abs(getMaxWidth(splitter, splitLines) - (float) width);
            if (maxWidth <= 10.0F) {
                return splitLines;
            }

            if (maxWidth < max) {
                max = maxWidth;
                list = splitLines;
            }
        }

        return list;
    }

}
