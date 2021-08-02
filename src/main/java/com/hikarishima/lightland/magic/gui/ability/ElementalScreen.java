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
public class ElementalScreen extends AbstractAbilityScreen {

    public static final ITextComponent TITLE = Translator.get("screen.ability.elemental.title");

    public static boolean canAccess() {
        MagicHandler handler = MagicHandler.get(Proxy.getPlayer());
        return handler.abilityPoints.canLevelElement() ||
                MagicRegistry.ELEMENT.getValues().stream()
                        .anyMatch(e -> handler.magicHolder.getElementalMastery(e) > 0);
    }

    protected ElementalScreen() {
        super(AbilityTab.ELEMENT, TITLE);
    }

    @Override
    protected void renderInside(MatrixStack matrix, int x0, int y0, int mx, int my, float partial) {
        //TODO
    }

}
