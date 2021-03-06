package com.hikarishima.lightland.magic.registry.entity.golem.render;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.registry.entity.golem.AlchemyGolemEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BipedGolemRenderer<T extends AlchemyGolemEntity> extends BipedRenderer<T, BipedGolemModel<T>> {
    private static final ResourceLocation SMALL = new ResourceLocation(LightLandMagic.MODID, "textures/entity/alchemy_golem/small_golem.png");
    private static final ResourceLocation MEDIUM = new ResourceLocation(LightLandMagic.MODID, "textures/entity/alchemy_golem/medium_golem.png");

    public BipedGolemRenderer(EntityRendererManager p_i50974_1_) {
        super(p_i50974_1_, new BipedGolemModel<>(0, false), 0.5F);
        addLayer(new BipedGolemLayer<>(this, new BipedGolemModel<>(0, true)));
        addLayer(new BipedArmorLayer<>(this, new BipedGolemModel<>(0.5f, true), new BipedGolemModel<>(1, true)));
    }

    public ResourceLocation getTextureLocation(T entity) {
        return entity.isBaby() ? SMALL : MEDIUM;
    }

}
