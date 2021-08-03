package com.hikarishima.lightland.magic.gui.magic_tree;

import com.google.common.collect.Maps;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.MagicProductType;
import com.hikarishima.lightland.proxy.Proxy;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;
import java.util.Map;

@ParametersAreNonnullByDefault
public class MagicTreeScreen extends Screen {

    private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
    private static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");
    private static final ITextComponent VERY_SAD_LABEL = new TranslationTextComponent("advancements.sad_label");
    private static final ITextComponent NO_ADVANCEMENTS_LABEL = new TranslationTextComponent("advancements.empty");
    private static final ITextComponent TITLE = new TranslationTextComponent("gui.advancements");

    private static final MagicProductType<?, ?>[] TABS = {MagicRegistry.MPT_CRAFT, MagicRegistry.MPT_EFF, MagicRegistry.MPT_ENCH, MagicRegistry.MPT_SPELL, MagicRegistry.MPT_ARCANE};

    public final ClientPlayerEntity player;
    public final MagicHandler handler;
    public final Map<MagicProductType<?, ?>, MagicTreeGui<?, ?>> tabs = Maps.newLinkedHashMap();

    private MagicTreeGui<?, ?> selected = null;
    private boolean isScrolling = false;

    public MagicTreeScreen() {
        super(TITLE);
        this.player = Proxy.getClientPlayer();
        handler = MagicHandler.get(player);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void init() {
        tabs.clear();
        for (MagicProductType<?, ?> type : TABS) {
            tabs.put(type.getAsType(), new MagicTreeGui(this, type, tabs.size()));
        }
        selected = tabs.values().iterator().next();
        for (IMagicRecipe<?> r : handler.magicHolder.listRecipe()) {
            ((MagicTreeGui) tabs.get(r.product_type.getAsType())).addWidget(handler.magicHolder.getProduct(r));
        }
    }

    public void render(MatrixStack matrix, int mx, int my, float partialTick) {
        int x0 = (this.width - 252) / 2;
        int y0 = (this.height - 140) / 2;
        this.renderBackground(matrix);

        this.renderInside(matrix, x0, y0);
        this.renderWindow(matrix, x0, y0);
        this.renderTooltips(matrix, mx, my, x0, y0);
    }

    private void renderInside(MatrixStack matrix, int x0, int y0) {
        if (selected == null) {
            fill(matrix, x0 + 9, y0 + 18, x0 + 9 + 234, y0 + 18 + 113, -16777216);
            int i = x0 + 9 + 117;
            drawCenteredString(matrix, this.font, NO_ADVANCEMENTS_LABEL, i, y0 + 18 + 56 - 4, -1);
            drawCenteredString(matrix, this.font, VERY_SAD_LABEL, i, y0 + 18 + 113 - 9, -1);
        } else {
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float) (x0 + 9), (float) (y0 + 18), 0.0F);
            selected.drawContents(matrix);
            RenderSystem.popMatrix();
            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
        }

    }

    private void renderWindow(MatrixStack matrix, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        TextureManager tm = Minecraft.getInstance().getTextureManager();
        tm.bind(WINDOW_LOCATION);
        this.blit(matrix, x, y, 0, 0, 252, 140);
        if (this.tabs.size() > 1) {
            tm.bind(TABS_LOCATION);
            Iterator<MagicTreeGui<?, ?>> iterator;
            iterator = this.tabs.values().iterator();
            while (iterator.hasNext()) {
                MagicTreeGui<?, ?> gui = iterator.next();
                gui.drawTab(matrix, x, y, gui == selected);
            }
            RenderSystem.enableRescaleNormal();
            RenderSystem.defaultBlendFunc();
            iterator = this.tabs.values().iterator();
            while (iterator.hasNext()) {
                MagicTreeGui<?, ?> gui = iterator.next();
                gui.drawIcon(x, y, this.itemRenderer);
            }
            RenderSystem.disableBlend();
        }
        this.font.draw(matrix, TITLE, (float) (x + 8), (float) (y + 6), 4210752);
    }

    private void renderTooltips(MatrixStack matrix, int mx, int my, int x0, int y0) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (selected != null) {
            RenderSystem.pushMatrix();
            RenderSystem.enableDepthTest();
            RenderSystem.translatef((float) (x0 + 9), (float) (y0 + 18), 400.0F);
            selected.drawTooltips(matrix, mx - x0 - 9, my - y0 - 18, x0, y0);
            RenderSystem.disableDepthTest();
            RenderSystem.popMatrix();
        }

        if (this.tabs.size() > 1) {
            for (MagicTreeGui<?, ?> gui : this.tabs.values()) {
                if (gui.isMouseOver(x0, y0, mx, my)) {
                    renderTooltip(matrix, gui.getTitle(), mx, my);
                }
            }
        }

    }

    public boolean mouseDragged(double x0, double y0, int button, double dx, double dy) {
        if (button != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else if (this.selected != null) {
                this.selected.scroll(dx, dy);
            }

            return true;
        }
    }

    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            int i = (this.width - 252) / 2;
            int j = (this.height - 140) / 2;
            for (MagicTreeGui<?, ?> gui : this.tabs.values()) {
                if (gui.isMouseOver(i, j, mx, my)) {
                    this.selected = gui;
                    break;
                }
            }
            if (selected.mouseClicked(i, j, (int) Math.round(mx - i - 9), (int) Math.round(my - j - 18))) {
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }


}
