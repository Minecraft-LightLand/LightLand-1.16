package com.hikarishima.lightland.magic.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MagicBookScreen extends ContainerScreen<MagicBookContainer> {

    public MagicBookScreen(MagicBookContainer cont, PlayerInventory inv, ITextComponent title) {
        super(cont, inv, title);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float v, int i, int i1) {

    }
}
