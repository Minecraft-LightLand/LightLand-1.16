package com.hikarishima.lightland.magic.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;

@OnlyIn(Dist.CLIENT)
public class MagicBookScreen extends ContainerScreen<MagicBookContainer> {

    public MagicBookScreen(MagicBookContainer cont, PlayerInventory inv, ITextComponent title) {
        super(cont, inv, title);
        LogManager.getLogger().info("magic book screen initialized");
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float v, int i, int i1) {

    }
}
