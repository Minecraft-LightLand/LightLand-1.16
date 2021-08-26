package com.hikarishima.lightland.magic.gui.ability;

import com.hikarishima.lightland.magic.gui.GuiTabType;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractAbilityScreen extends Screen {

    private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
    private static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");

    protected final AbilityTab tab;

    protected AbstractAbilityScreen(AbilityTab tab, ITextComponent title) {
        super(title);
        this.tab = tab;
    }

    @Override
    public final void render(MatrixStack matrix, int mx, int my, float partial) {
        int x0 = (this.width - 252) / 2;
        int y0 = (this.height - 140) / 2;
        renderBackground(matrix);
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) (x0 + 9), (float) (y0 + 18), 0.0F);
        renderInside(matrix, 234, 113, mx - x0 - 9, my - y0 - 18, partial);
        RenderSystem.popMatrix();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        TextureManager tm = Minecraft.getInstance().getTextureManager();
        ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        tm.bind(WINDOW_LOCATION);
        this.blit(matrix, x0, y0, 0, 0, 252, 140);
        tm.bind(TABS_LOCATION);
        for (AbilityTab tab : AbilityTab.values()) {
            tab.type.draw(matrix, this, x0, y0, this.tab == tab, tab.index);
        }
        RenderSystem.enableRescaleNormal();
        RenderSystem.defaultBlendFunc();
        for (AbilityTab tab : AbilityTab.values()) {
            tab.type.drawIcon(x0, y0, tab.index, ir, tab.icon);
        }
        RenderSystem.disableBlend();
        this.font.draw(matrix, tab.title, (float) (x0 + 8), (float) (y0 + 6), 4210752);
        for (AbilityTab tab : AbilityTab.values()) {
            if (tab.type.isMouseOver(x0, y0, tab.index, mx, my)) {
                renderTooltip(matrix, tab.title, mx, my);
            }
        }
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) (x0 + 9), (float) (y0 + 18), 0.0F);
        renderInnerTooltip(matrix, 234, 113, mx - x0 - 9, my - y0 - 18);
        RenderSystem.popMatrix();
    }

    protected abstract void renderInside(MatrixStack matrix, int w, int h, int mx, int my, float partial);

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int x0 = (this.width - 252) / 2;
        int y0 = (this.height - 140) / 2;
        if (button == 0) {
            for (AbilityTab tab : AbilityTab.values()) {
                if (tab != this.tab && tab.type.isMouseOver(x0, y0, tab.index, mx, my) && tab.pred.getAsBoolean()) {
                    Minecraft.getInstance().setScreen(tab.sup.get());
                    return true;
                }
            }
            if (innerMouseClick(234, 113, mx - x0 - 9, my - y0 - 18))
                return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    public abstract boolean innerMouseClick(int w, int h, double mx, double my);

    public abstract void renderInnerTooltip(MatrixStack matrix, int w, int h, int mx, int my);

    public enum AbilityTab {
        PROFESSION(0, Items.IRON_SWORD.getDefaultInstance(), ProfessionScreen::canAccess, ProfessionScreen::new, ProfessionScreen.TITLE),
        ABILITY(1, Items.GOLDEN_APPLE.getDefaultInstance(), () -> true, AbilityScreen::new, AbilityScreen.TITLE),
        ELEMENT(2, MagicItemRegistry.MAGIC_BOOK.getDefaultInstance(), ElementalScreen::canAccess, ElementalScreen::new, ElementalScreen.TITLE),
        ARCANE(3, MagicItemRegistry.ARCANE_AXE_GILDED.getDefaultInstance(), ArcaneScreen::canAccess, ArcaneScreen::new, ArcaneScreen.TITLE);

        public final GuiTabType type = GuiTabType.ABOVE;
        public final int index;
        public final ItemStack icon;
        public final BooleanSupplier pred;
        public final Supplier<? extends AbstractAbilityScreen> sup;
        public final ITextComponent title;

        AbilityTab(int index, ItemStack icon, BooleanSupplier pred, Supplier<? extends AbstractAbilityScreen> sup, ITextComponent title) {
            this.index = index;
            this.icon = icon;
            this.pred = pred;
            this.sup = sup;
            this.title = title;
        }
    }

}
