package com.hikarishima.lightland.magic.registry.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RitualRenderer extends TileEntityRenderer<RitualTE> {

    public RitualRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(RitualTE te, float partial, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay) {
        float time = Math.floorMod(Objects.requireNonNull(te.getLevel()).getGameTime(), 80L) + partial;
        ItemStack is = te.getItem(0);
        if (!is.isEmpty()) {
            matrix.pushPose();
            double offset = (Math.sin(time * 2 * Math.PI / 40.0) - 3) / 16;
            matrix.translate(0.5, 1.5 + offset, 0.5);
            matrix.mulPose(Vector3f.YP.rotationDegrees(time * 4.5f));
            Minecraft.getInstance().getItemRenderer().renderStatic(is, ItemCameraTransforms.TransformType.GROUND, light,
                    overlay, matrix, buffer);
            matrix.popPose();
        }
    }
}
