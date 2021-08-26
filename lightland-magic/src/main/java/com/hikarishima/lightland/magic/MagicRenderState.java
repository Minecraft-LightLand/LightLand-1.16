package com.hikarishima.lightland.magic;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MagicRenderState extends RenderState {

    public static final ResourceLocation RL_ENTITY_BODY_ICON = new ResourceLocation(LightLandMagic.MODID, "textures/arcane_icon.png");

    public static RenderType get2DIcon(ResourceLocation rl) {
        return RenderType.create(
                "entity_body_icon",
                DefaultVertexFormats.POSITION_TEX,
                7, 256, false, true,
                RenderType.State.builder()
                        .setTextureState(new TextureState(rl, false, false))
                        .setAlphaState(RenderState.DEFAULT_ALPHA)
                        .setTransparencyState(ADDITIVE_TRANSPARENCY)
                        .setDepthTestState(NO_DEPTH_TEST)
                        .setFogState(NO_FOG)
                        .createCompositeState(false)
        );
    }

    public static RenderType getSpell() {
        return RenderType.create(
                "spell_blend_notex",
                DefaultVertexFormats.POSITION_COLOR,
                7, 256, true, true,
                RenderType.State.builder()
                        .setTextureState(RenderState.NO_TEXTURE)
                        .setAlphaState(RenderState.DEFAULT_ALPHA)
                        .setCullState(NO_CULL)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(false)
        );
    }

    private MagicRenderState(String str, Runnable a, Runnable b) {
        super(str, a, b);
    }
}
