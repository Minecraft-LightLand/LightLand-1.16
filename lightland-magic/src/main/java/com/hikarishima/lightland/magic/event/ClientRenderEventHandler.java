package com.hikarishima.lightland.magic.event;

import com.hikarishima.lightland.magic.registry.VanillaMagicRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("unused")
public class ClientRenderEventHandler {

    private static final RenderType TYPE = RenderType.create(
            "entity_body_icon",
            DefaultVertexFormats.POSITION_TEX,
            7, 256, false, true,
            MagicRenderState.ENTITY_BODY_ICON
    );

    @SubscribeEvent
    public void onLivingEntityRender(RenderLivingEvent.Post<?, ?> event) {
        LivingEntity entity = event.getEntity();
        LivingRenderer<?, ?> renderer = event.getRenderer();
        EffectInstance ins = entity.getEffect(VanillaMagicRegistry.ARCANE);
        if (ins != null) {
            renderArcaneIcon(entity, event.getMatrixStack(), event.getBuffers(), renderer.getDispatcher());
        }
    }

    private static void renderArcaneIcon(Entity entity, MatrixStack matrix, IRenderTypeBuffer buffer, EntityRendererManager manager) {
        float f = entity.getBbHeight() / 2;
        matrix.pushPose();
        matrix.translate(0.0D, f, 0.0D);
        matrix.mulPose(manager.cameraOrientation());
        MatrixStack.Entry entry = matrix.last();
        IVertexBuilder ivertexbuilder = buffer.getBuffer(TYPE);
        iconVertex(entry, ivertexbuilder, 0.5f, -0.5f, 0, 0, 1);
        iconVertex(entry, ivertexbuilder, -0.5f, -0.5f, 0, 1, 1);
        iconVertex(entry, ivertexbuilder, -0.5f, 0.5f, 0, 1, 0);
        iconVertex(entry, ivertexbuilder, 0.5f, 0.5f, 0, 0, 0);
        matrix.popPose();
    }

    private static void iconVertex(MatrixStack.Entry entry, IVertexBuilder builder, float x, float y, float z, float u, float v) {
        builder.vertex(entry.pose(), x, y, z)
                .uv(u, v)
                .normal(entry.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

}
