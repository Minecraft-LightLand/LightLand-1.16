package com.hikarishima.lightland.magic.gui.ability;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.proxy.Proxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ProfessionScreen extends AbstractAbilityScreen {

    public static final ITextComponent TITLE = Translator.get("screen.ability.profession.title");

    public static boolean canAccess() {
        return MagicHandler.get(Proxy.getPlayer()).abilityPoints.getProfession() == null;
    }

    public ProfessionScreen() {
        super(AbilityTab.PROFESSION, TITLE);
    }

    @Override
    protected void renderInside(MatrixStack matrix, int x0, int y0, int mx, int my, float partial) {
        //TODO
    }

}
