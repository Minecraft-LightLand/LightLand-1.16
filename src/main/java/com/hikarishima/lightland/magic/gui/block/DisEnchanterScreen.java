package com.hikarishima.lightland.magic.gui.block;

import com.hikarishima.lightland.config.Translator;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DisEnchanterScreen extends ContainerScreen<DisEnchanterContainer> {

    public DisEnchanterScreen(DisEnchanterContainer cont, PlayerInventory plInv, ITextComponent title) {
        super(cont, plInv, title);
    }

    @Override
    protected void renderBg(MatrixStack matrix, float partial, int x0, int y0) {

    }
}
