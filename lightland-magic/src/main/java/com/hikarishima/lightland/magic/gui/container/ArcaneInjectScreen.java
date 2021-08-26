package com.hikarishima.lightland.magic.gui.container;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicProxy;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.gui.AbstractHexGui;
import com.lcy0x1.core.util.SpriteManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public class ArcaneInjectScreen extends AbstractScreen<ArcaneInjectContainer> implements ExtraInfo<Map.Entry<MagicElement, Integer>> {

    public ArcaneInjectScreen(ArcaneInjectContainer cont, PlayerInventory plInv, ITextComponent title) {
        super(cont, plInv, title);
    }

    @Override
    protected void renderBg(MatrixStack matrix, float partial, int mx, int my) {
        mx -= getGuiLeft();
        my -= getGuiTop();
        SpriteManager sm = menu.sm;
        SpriteManager.ScreenRenderer sr = sm.getRenderer(this);
        sr.start(matrix);
        if (menu.err == ArcaneInjectContainer.Error.PASS)
            sr.draw(matrix, "arrow", sm.within("arrow", mx, my) ? "arrow_2" : "arrow_1");
        else if (menu.err != ArcaneInjectContainer.Error.NO_ITEM)
            sr.draw(matrix, "arrow", "arrow_3");
        getInfo((ex, ey, w, h, ent) -> {
            int count = ent.getValue();
            int have = MagicProxy.getHandler().magicHolder.getElement(ent.getKey());
            AbstractHexGui.drawElement(matrix, ex + getGuiLeft() + 9, ey + getGuiTop() + 9, ent.getKey(), "" + count, have >= count ? 0xFFFFFF : 0xFF0000);
        });
    }

    @Override
    protected void renderTooltip(MatrixStack matrix, int mx, int my) {
        super.renderTooltip(matrix, mx, my);
        if (menu.sm.within("arrow", mx, my) &&
                menu.err != ArcaneInjectContainer.Error.NO_ITEM)
            renderTooltip(matrix, menu.err.getDesc(menu), mx, my);
        getInfoMouse(mx - getGuiLeft(), my - getGuiTop(), (ex, ey, w, h, ent) -> {
            int count = ent.getValue();
            int have = MagicProxy.getHandler().magicHolder.getElement(ent.getKey());
            IFormattableTextComponent text = Translator.get(have < count, "screen.spell_craft.elem_cost", count, have);
            renderTooltip(matrix, text, mx, my);
        });
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        SpriteManager sm = menu.sm;
        if (menu.err == ArcaneInjectContainer.Error.PASS && sm.within("arrow", mx - getGuiLeft(), my - getGuiTop())) {
            click(0);
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public void getInfo(Con<Map.Entry<MagicElement, Integer>> con) {
        int x = menu.sm.getComp("output_slot").x + 18 - 1;
        int y = menu.sm.getComp("output_slot").y - 1;
        int i = 0;
        for (Map.Entry<MagicElement, Integer> ent : menu.map.entrySet()) {
            int ex = x + i % 3 * 18;
            int ey = y + i / 3 * 18;
            con.apply(ex, ey, 18, 18, ent);
            i++;
        }
    }

}
