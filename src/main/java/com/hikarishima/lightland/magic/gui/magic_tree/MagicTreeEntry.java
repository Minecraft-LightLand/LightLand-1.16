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
import net.minecraft.client.gui.advancements.AdvancementState;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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
        int title_width = 29 + minecraft.font.width(this.title);
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
        if (product.visible()) {
            this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
            this.blit(matrix, x0 + this.x + 3, y0 + this.y, this.display.getFrame().getTexture(), 128 + state.getIndex() * 26, 26, 26);
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
        boolean lvt_7_1_ = x0 + sx + this.x + this.width + 26 >= this.tab.getScreen().width;
        String lvt_8_1_ = null;//this.progress == null ? null : this.progress.getProgressText();
        int lvt_9_1_ = lvt_8_1_ == null ? 0 : this.minecraft.font.width(lvt_8_1_);
        int var10000 = 113 - sy - this.y - 26;
        int var10002 = this.description.size();
        boolean lvt_10_1_ = var10000 <= 6 + var10002 * 9;
        float lvt_11_1_ = 0;//this.progress == null ? 0.0F : this.progress.getPercent();
        int lvt_15_1_ = MathHelper.floor(lvt_11_1_ * (float) this.width);
        AdvancementState lvt_12_4_;
        AdvancementState lvt_13_4_;
        AdvancementState lvt_14_4_;
        if (lvt_11_1_ >= 1.0F) {
            lvt_15_1_ = this.width / 2;
            lvt_12_4_ = AdvancementState.OBTAINED;
            lvt_13_4_ = AdvancementState.OBTAINED;
            lvt_14_4_ = AdvancementState.OBTAINED;
        } else if (lvt_15_1_ < 2) {
            lvt_15_1_ = this.width / 2;
            lvt_12_4_ = AdvancementState.UNOBTAINED;
            lvt_13_4_ = AdvancementState.UNOBTAINED;
            lvt_14_4_ = AdvancementState.UNOBTAINED;
        } else if (lvt_15_1_ > this.width - 2) {
            lvt_15_1_ = this.width / 2;
            lvt_12_4_ = AdvancementState.OBTAINED;
            lvt_13_4_ = AdvancementState.OBTAINED;
            lvt_14_4_ = AdvancementState.UNOBTAINED;
        } else {
            lvt_12_4_ = AdvancementState.OBTAINED;
            lvt_13_4_ = AdvancementState.UNOBTAINED;
            lvt_14_4_ = AdvancementState.UNOBTAINED;
        }

        int lvt_16_1_ = this.width - lvt_15_1_;
        minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        int lvt_17_1_ = sy + this.y;
        int lvt_18_2_;
        if (lvt_7_1_) {
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

        this.blit(matrix, lvt_18_2_, lvt_17_1_, 0, lvt_12_4_.getIndex() * 26, lvt_15_1_, 26);
        this.blit(matrix, lvt_18_2_ + lvt_15_1_, lvt_17_1_, 200 - lvt_16_1_, lvt_13_4_.getIndex() * 26, lvt_16_1_, 26);
        this.blit(matrix, sx + this.x + 3, sy + this.y, this.display.getFrame().getTexture(), 128 + lvt_14_4_.getIndex() * 26, 26, 26);
        if (lvt_7_1_) {
            minecraft.font.drawShadow(matrix, this.title, (float) (lvt_18_2_ + 5), (float) (sy + this.y + 9), -1);
            if (lvt_8_1_ != null) {
                this.minecraft.font.drawShadow(matrix, lvt_8_1_, (float) (sx + this.x - lvt_9_1_), (float) (sy + this.y + 9), -1);
            }
        } else {
            minecraft.font.drawShadow(matrix, this.title, (float) (sx + this.x + 32), (float) (sy + this.y + 9), -1);
            if (lvt_8_1_ != null) {
                minecraft.font.drawShadow(matrix, lvt_8_1_, (float) (sx + this.x + this.width - lvt_9_1_ - 5), (float) (sy + this.y + 9), -1);
            }
        }

        FontRenderer fontRenderer = minecraft.font;
        for (int i = 0; i < this.description.size(); ++i) {
            IReorderingProcessor var22 = this.description.get(i);
            float var10003 = (float) (lvt_18_2_ + 5);
            int var10004 = lvt_10_1_ ? lvt_17_1_ + 26 - lvt_19_1_ + 7 : sy + this.y + 9 + 17;
            fontRenderer.draw(matrix, var22, var10003, (float) (var10004 + i * 9), -5592406);
        }

        minecraft.getItemRenderer().renderAndDecorateFakeItem(this.display.getIcon(), sx + this.x + 8, sy + this.y + 5);
    }

    private void render9Sprite(MatrixStack matrix, int p_238691_2_, int p_238691_3_, int p_238691_4_, int p_238691_5_, int p_238691_6_, int p_238691_7_, int p_238691_8_, int p_238691_9_, int p_238691_10_) {
        this.blit(matrix, p_238691_2_, p_238691_3_, p_238691_9_, p_238691_10_, p_238691_6_, p_238691_6_);
        this.renderRepeating(matrix, p_238691_2_ + p_238691_6_, p_238691_3_, p_238691_4_ - p_238691_6_ - p_238691_6_, p_238691_6_, p_238691_9_ + p_238691_6_, p_238691_10_, p_238691_7_ - p_238691_6_ - p_238691_6_, p_238691_8_);
        this.blit(matrix, p_238691_2_ + p_238691_4_ - p_238691_6_, p_238691_3_, p_238691_9_ + p_238691_7_ - p_238691_6_, p_238691_10_, p_238691_6_, p_238691_6_);
        this.blit(matrix, p_238691_2_, p_238691_3_ + p_238691_5_ - p_238691_6_, p_238691_9_, p_238691_10_ + p_238691_8_ - p_238691_6_, p_238691_6_, p_238691_6_);
        this.renderRepeating(matrix, p_238691_2_ + p_238691_6_, p_238691_3_ + p_238691_5_ - p_238691_6_, p_238691_4_ - p_238691_6_ - p_238691_6_, p_238691_6_, p_238691_9_ + p_238691_6_, p_238691_10_ + p_238691_8_ - p_238691_6_, p_238691_7_ - p_238691_6_ - p_238691_6_, p_238691_8_);
        this.blit(matrix, p_238691_2_ + p_238691_4_ - p_238691_6_, p_238691_3_ + p_238691_5_ - p_238691_6_, p_238691_9_ + p_238691_7_ - p_238691_6_, p_238691_10_ + p_238691_8_ - p_238691_6_, p_238691_6_, p_238691_6_);
        this.renderRepeating(matrix, p_238691_2_, p_238691_3_ + p_238691_6_, p_238691_6_, p_238691_5_ - p_238691_6_ - p_238691_6_, p_238691_9_, p_238691_10_ + p_238691_6_, p_238691_7_, p_238691_8_ - p_238691_6_ - p_238691_6_);
        this.renderRepeating(matrix, p_238691_2_ + p_238691_6_, p_238691_3_ + p_238691_6_, p_238691_4_ - p_238691_6_ - p_238691_6_, p_238691_5_ - p_238691_6_ - p_238691_6_, p_238691_9_ + p_238691_6_, p_238691_10_ + p_238691_6_, p_238691_7_ - p_238691_6_ - p_238691_6_, p_238691_8_ - p_238691_6_ - p_238691_6_);
        this.renderRepeating(matrix, p_238691_2_ + p_238691_4_ - p_238691_6_, p_238691_3_ + p_238691_6_, p_238691_6_, p_238691_5_ - p_238691_6_ - p_238691_6_, p_238691_9_ + p_238691_7_ - p_238691_6_, p_238691_10_ + p_238691_6_, p_238691_7_, p_238691_8_ - p_238691_6_ - p_238691_6_);
    }

    private void renderRepeating(MatrixStack matrix, int p_238690_2_, int p_238690_3_, int p_238690_4_, int p_238690_5_, int p_238690_6_, int p_238690_7_, int p_238690_8_, int p_238690_9_) {
        for (int lvt_10_1_ = 0; lvt_10_1_ < p_238690_4_; lvt_10_1_ += p_238690_8_) {
            int lvt_11_1_ = p_238690_2_ + lvt_10_1_;
            int lvt_12_1_ = Math.min(p_238690_8_, p_238690_4_ - lvt_10_1_);

            for (int i = 0; i < p_238690_5_; i += p_238690_9_) {
                int lvt_14_1_ = p_238690_3_ + i;
                int lvt_15_1_ = Math.min(p_238690_9_, p_238690_5_ - i);
                this.blit(matrix, lvt_11_1_, lvt_14_1_, p_238690_6_, p_238690_7_, lvt_12_1_, lvt_15_1_);
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
