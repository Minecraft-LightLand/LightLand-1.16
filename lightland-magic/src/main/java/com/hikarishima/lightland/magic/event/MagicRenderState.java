package com.hikarishima.lightland.magic.event;

import com.hikarishima.lightland.magic.LightLandMagic;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;

public class MagicRenderState extends RenderState {

    private static final ResourceLocation RL_ENTITY_BODY_ICON = new ResourceLocation(LightLandMagic.MODID, "textures/arcane_icon.png");

    public static final RenderType.State ENTITY_BODY_ICON = RenderType.State.builder()
            .setTextureState(new TextureState(RL_ENTITY_BODY_ICON, false, false))
            .setAlphaState(RenderState.DEFAULT_ALPHA)
            .setTransparencyState(ADDITIVE_TRANSPARENCY)
            .setDepthTestState(NO_DEPTH_TEST)
            .setFogState(NO_FOG)
            .createCompositeState(false);

    public MagicRenderState(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_) {
        super(p_i225973_1_, p_i225973_2_, p_i225973_3_);
    }
}
