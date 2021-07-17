package com.hikarishima.lightland.magic.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisEnchantScreen extends ContainerScreen<DisEnchantContainer> {

    private final DisEnchantContainer cont;

    public DisEnchantScreen(DisEnchantContainer cont, PlayerInventory inv, ITextComponent title) {
        super(cont, inv, title);
        this.cont = cont;
    }

    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float v, int i, int i1) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

    }
}
