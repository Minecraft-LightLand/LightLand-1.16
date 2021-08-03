package com.hikarishima.lightland.magic.gui.ability;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.proxy.Proxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArcaneScreen extends AbstractAbilityScreen {

    public static final ITextComponent TITLE = Translator.get("screen.ability.arcane.title");

    public static boolean canAccess() {
        MagicHandler handler = MagicHandler.get(Proxy.getPlayer());
        return handler.abilityPoints.canLevelArcane() ||
                MagicRegistry.ARCANE_TYPE.getValues().stream()
                        .anyMatch(handler.magicAbility::isArcaneTypeUnlocked);
    }

    protected ArcaneScreen() {
        super(AbilityTab.ARCANE, TITLE);
    }

    @Override
    protected void renderInside(MatrixStack matrix, int w, int h, int mx, int my, float partial) {
        //TODO
    }

    @Override
    public boolean innerMouseClick(int w, int h, double mx, double my) {
        return false;
    }

    @Override
    public void renderInnerTooltip(MatrixStack matrix, int w, int h, int mx, int my) {

    }

}
