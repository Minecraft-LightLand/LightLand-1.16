package com.hikarishima.lightland.magic.registry.entity.misc;

import com.hikarishima.lightland.magic.MagicRenderState;
import com.hikarishima.lightland.magic.spell.render.SpellComponent;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SpellEntityRenderer extends EntityRenderer<SpellEntity> {

    public SpellEntityRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void render(SpellEntity entity, float yRot, float partial, MatrixStack matrix, IRenderTypeBuffer buffer, int light) {
        SpellComponent.RenderHandle handle = new SpellComponent.RenderHandle(matrix, buffer.getBuffer(MagicRenderState.getSpell()), entity.tickCount + partial, light);
        matrix.pushPose();
        matrix.translate(0, 1.5f, 0);
        matrix.mulPose(Vector3f.YP.rotationDegrees(-entity.yRot));
        matrix.mulPose(Vector3f.XP.rotationDegrees(entity.xRot));
        float scale = entity.getSize(partial);
        matrix.scale(scale / 16f, scale / 16f, scale / 16f);
        entity.getComponent().render(handle);
        matrix.popPose();
        super.render(entity, yRot, partial, matrix, buffer, light);
    }

    @Override
    public ResourceLocation getTextureLocation(SpellEntity p_110775_1_) {
        return null;
    }
}
