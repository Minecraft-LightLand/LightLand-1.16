package com.hikarishima.lightland.magic.gui.container.experimental;

import com.hikarishima.lightland.magic.gui.container.AbstractScreen;
import com.lcy0x1.core.util.SpriteManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MagicCraftScreen extends AbstractScreen<MagicCraftContainer> {

    public MagicCraftScreen(MagicCraftContainer cont, PlayerInventory plInv, ITextComponent title) {
        super(cont, plInv, title);
    }

    @Override
    protected void renderBg(MatrixStack matrix, float partial, int mx, int my) {
        mx -= getGuiLeft();
        my -= getGuiTop();
        SpriteManager sm = menu.sm;
        SpriteManager.ScreenRenderer sr = sm.getRenderer(this);
        sr.start(matrix);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        SpriteManager sm = menu.sm;
        if (!menu.slot.isEmpty() && sm.within("arrow_input", mx - getGuiLeft(), my - getGuiTop())) {
            click(0);
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

}
