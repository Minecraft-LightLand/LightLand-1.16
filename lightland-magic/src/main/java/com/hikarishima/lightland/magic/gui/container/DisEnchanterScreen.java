package com.hikarishima.lightland.magic.gui.container;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.gui.AbstractHexGui;
import com.lcy0x1.core.util.SpriteManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public class DisEnchanterScreen extends AbstractScreen<DisEnchanterContainer> implements ExtraInfo<Map.Entry<MagicElement, Integer>> {

    public DisEnchanterScreen(DisEnchanterContainer cont, PlayerInventory plInv, ITextComponent title) {
        super(cont, plInv, title);
    }

    @Override
    protected void renderBg(MatrixStack matrix, float partial, int mx, int my) {
        mx -= getGuiLeft();
        my -= getGuiTop();
        SpriteManager sm = menu.sm;
        SpriteManager.ScreenRenderer sr = sm.getRenderer(this);
        sr.start(matrix);
        if (!menu.map.isEmpty()) {
            sr.draw(matrix, "arrow", sm.within("arrow", mx, my) ? "arrow_2" : "arrow_1");
            getInfo((x, y, w, h, ent) -> {
                AbstractHexGui.drawElement(matrix, x, y, ent.getKey(), "" + ent.getValue());
            });
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        SpriteManager sm = menu.sm;
        if (!menu.slot.isEmpty() && sm.within("arrow", mx - getGuiLeft(), my - getGuiTop())) {
            click(0);
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public void getInfo(Con<Map.Entry<MagicElement, Integer>> con) {
        if (!menu.map.isEmpty()) {
            int x = 8 + 8 + 18 * 2 + getGuiLeft();
            int y = menu.sm.getComp("main_slot").y + getGuiTop() + 8;
            for (Map.Entry<MagicElement, Integer> ent : menu.map.entrySet()) {
                con.apply(x += 18, y, 16, 16, ent);
            }
        }
    }
}
