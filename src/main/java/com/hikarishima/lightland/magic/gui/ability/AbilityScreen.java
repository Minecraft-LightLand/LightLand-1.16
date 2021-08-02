package com.hikarishima.lightland.magic.gui.ability;

import com.hikarishima.lightland.config.Translator;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AbilityScreen extends AbstractAbilityScreen {

    public static final ITextComponent TITLE = Translator.get("screen.ability.ability.title");

    protected AbilityScreen() {
        super(AbilityTab.ABILITY, TITLE);
    }

    @Override
    protected void renderInside(MatrixStack matrix, int x0, int y0, int mx, int my, float partial) {
        //TODO
    }

}
