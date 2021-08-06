package com.hikarishima.lightland.magic.gui.container;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.gui.AbstractHexGui;
import com.hikarishima.lightland.proxy.Proxy;
import com.lcy0x1.core.util.SpriteManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SpellCraftScreen extends AbstractScreen<SpellCraftContainer> {

    public SpellCraftScreen(SpellCraftContainer cont, PlayerInventory plInv, ITextComponent title) {
        super(cont, plInv, title);
        this.imageHeight = SpellCraftContainer.MANAGER.getHeight();
        this.inventoryLabelY = SpellCraftContainer.MANAGER.getPlInvY() - 11;
    }

    @Override
    protected void renderBg(MatrixStack matrix, float partial, int mx, int my) {
        mx -= getGuiLeft();
        my -= getGuiTop();
        SpriteManager sm = SpellCraftContainer.MANAGER;
        SpriteManager.ScreenRenderer sr = sm.getRenderer(this);
        sr.start(matrix);
        if (menu.err == SpellCraftContainer.Error.PASS)
            sr.draw(matrix, "arrow", sm.within("arrow", mx, my) ? "arrow_2" : "arrow_1");
        else if (menu.err != SpellCraftContainer.Error.NO_ITEM)
            sr.draw(matrix, "arrow", "arrow_3");
        int x = sm.getComp("output_slot").x + 18 + 8 + getGuiLeft();
        int y = sm.getComp("output_slot").y + 8 + getGuiTop();
        int i = 0;
        for (MagicElement elem : menu.map.keySet()) {
            int count = menu.map.get(elem);
            int ex = x + i % 3 * 18;
            int ey = y + i / 3 * 18;
            int have = MagicHandler.get(Proxy.getClientPlayer()).magicHolder.getElement(elem);
            AbstractHexGui.drawElement(matrix, ex, ey, elem, "" + count, have >= count ? 0xFFFFFF : 0xFF0000);
            i++;
        }
    }

    @Override
    protected void renderTooltip(MatrixStack matrix, int mx, int my) {
        super.renderTooltip(matrix, mx, my);
        if (SpellCraftContainer.MANAGER.within("arrow", mx - getGuiLeft(), my - getGuiTop()) &&
                menu.err != SpellCraftContainer.Error.NO_ITEM)
            renderTooltip(matrix, menu.err.getDesc(menu), mx, my);
        SpriteManager sm = SpellCraftContainer.MANAGER;
        int x = sm.getComp("output_slot").x + 18 + 8 + getGuiLeft();
        int y = sm.getComp("output_slot").y + 8 + getGuiTop();
        int i = 0;
        for (MagicElement elem : menu.map.keySet()) {
            int ex = x + i % 3 * 18;
            int ey = y + i / 3 * 18;
            if (mx > ex - 8 && mx < ex + 8 && my > ey - 8 && my < ey + 8) {
                int count = menu.map.get(elem);
                int have = MagicHandler.get(Proxy.getClientPlayer()).magicHolder.getElement(elem);
                IFormattableTextComponent text = Translator.get("screen.spell_craft.elem_cost", count, have);
                if (have < count)
                    text.withStyle(TextFormatting.RED);
                renderTooltip(matrix, text, mx, my);
            }
            i++;
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        SpriteManager sm = SpellCraftContainer.MANAGER;
        if (menu.err == SpellCraftContainer.Error.PASS && sm.within("arrow", mx - getGuiLeft(), my - getGuiTop())) {
            if (menu.clickMenuButton(Proxy.getClientPlayer(), 0)) {
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
            }
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

}
