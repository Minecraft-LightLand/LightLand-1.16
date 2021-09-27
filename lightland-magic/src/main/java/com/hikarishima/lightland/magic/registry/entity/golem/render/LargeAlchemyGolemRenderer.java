package com.hikarishima.lightland.magic.registry.entity.golem.render;

import com.hikarishima.lightland.magic.registry.entity.golem.LargeAlchemyGolemEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LargeAlchemyGolemRenderer extends MobRenderer<LargeAlchemyGolemEntity, LargeAlchemyGolemModel<LargeAlchemyGolemEntity>> {
    private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

    public LargeAlchemyGolemRenderer(EntityRendererManager p_i46133_1_) {
        super(p_i46133_1_, new LargeAlchemyGolemModel<>(), 0.7F);
    }

    public ResourceLocation getTextureLocation(LargeAlchemyGolemEntity p_110775_1_) {
        return GOLEM_LOCATION;
    }

    protected void setupRotations(LargeAlchemyGolemEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
        super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
        if (!((double)p_225621_1_.animationSpeed < 0.01D)) {
            float f = 13.0F;
            float f1 = p_225621_1_.animationPosition - p_225621_1_.animationSpeed * (1.0F - p_225621_5_) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            p_225621_2_.mulPose(Vector3f.ZP.rotationDegrees(6.5F * f2));
        }
    }
}