package com.hikarishima.lightland.magic.spell.render;

import com.hikarishima.lightland.magic.MagicRenderState;
import com.hikarishima.lightland.magic.registry.entity.SpellEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

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
        matrix.scale(1 / 16f, 1 / 16f, 1 / 16f);
        entity.getComponent().render(handle);
        matrix.popPose();
        super.render(entity, yRot, partial, matrix, buffer, light);
    }

    @Override
    public ResourceLocation getTextureLocation(SpellEntity p_110775_1_) {
        return null;
    }
}
