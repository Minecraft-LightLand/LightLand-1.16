package com.hikarishima.lightland.magic.registry.entity.golem.render;

import com.hikarishima.lightland.magic.registry.entity.golem.LargeAlchemyGolemEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LargeGolemLayer extends LayerRenderer<LargeAlchemyGolemEntity, LargeAlchemyGolemModel> {

    public enum Part {
        LEFT(0), BODY(1), RIGHT(2), LOWER(3);

        final int index;

        Part(int index) {
            this.index = index;
        }
    }

    private final LargeAlchemyGolemModel model;

    public LargeGolemLayer(IEntityRenderer<LargeAlchemyGolemEntity, LargeAlchemyGolemModel> renderer, LargeAlchemyGolemModel model) {
        super(renderer);
        this.model = model;
    }

    @Override
    public void render(MatrixStack matrix, IRenderTypeBuffer buffer, int light, LargeAlchemyGolemEntity entity, float a, float b, float c, float d, float e, float f) {
        for (Part part : Part.values()) {
            this.getParentModel().copyPropertiesTo(model);
            this.setPartVisibility(part);
            boolean foil = false;
            ResourceLocation rl = new ResourceLocation(entity.getMaterials().get(part.index));
            rl = new ResourceLocation(rl.getNamespace(), "textures/entity/alchemy_golem/large/" + rl.getPath());
            IVertexBuilder ivertexbuilder = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(rl), false, foil);
            model.renderToBuffer(matrix, ivertexbuilder, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        }
    }

    protected void setPartVisibility(Part part) {
        model.parts().forEach(e -> e.visible = false);
        switch (part) {
            case LEFT:
                model.arm0.visible = true;
                break;
            case RIGHT:
                model.arm1.visible = true;
                break;
            case LOWER:
                model.leg0.visible = true;
                model.leg1.visible = true;
                break;
            case BODY:
                model.body.visible = true;
                break;
        }
    }

}
