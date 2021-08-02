package com.hikarishima.lightland.magic.gui.ability;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.proxy.Proxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ProfessionScreen extends Screen {

    public static final ITextComponent TITLE = Translator.get("screen.profession.title");

    public boolean canAccess() {
        return MagicHandler.get(Proxy.getPlayer()).abilityPoints.getProfession() == null;
    }

    protected ProfessionScreen() {
        super(TITLE);
    }

    @Override
    public void render(MatrixStack matrix, int mx, int my, float partial) {
        super.render(matrix, mx, my, partial);
    }

}
