package com.hikarishima.lightland.magic.registry.entity.misc;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SpecialSpriteRenderer<T extends Entity & IRendersAsItem & ISizedItemEntity> extends EntityRenderer<T> {

    private final ItemRenderer itemRenderer;
    private final boolean fullBright;

    public SpecialSpriteRenderer(EntityRendererManager manager, ItemRenderer p_i226035_2_, boolean bright) {
        super(manager);
        this.itemRenderer = p_i226035_2_;
        this.fullBright = bright;
    }

    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return this.fullBright ? 15 : super.getBlockLightLevel(entity, pos);
    }

    public void render(T entity, float yRot, float partial, MatrixStack matrix, IRenderTypeBuffer buffer, int light) {
        if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25D)) {
            matrix.pushPose();
            float size = entity.getSize();
            matrix.scale(size, size, size);
            matrix.mulPose(this.entityRenderDispatcher.cameraOrientation());
            matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            this.itemRenderer.renderStatic(entity.getItem(), ItemCameraTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, matrix, buffer);
            matrix.popPose();
            super.render(entity, yRot, partial, matrix, buffer, light);
        }
    }

    public ResourceLocation getTextureLocation(Entity p_110775_1_) {
        return AtlasTexture.LOCATION_BLOCKS;
    }

}
