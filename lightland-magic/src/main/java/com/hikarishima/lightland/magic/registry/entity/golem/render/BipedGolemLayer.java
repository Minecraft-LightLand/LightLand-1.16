package com.hikarishima.lightland.magic.registry.entity.golem.render;

import com.hikarishima.lightland.magic.registry.entity.golem.AlchemyGolemEntity;
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
public class BipedGolemLayer<T extends AlchemyGolemEntity> extends LayerRenderer<T, BipedGolemModel<T>> {

    public enum Part {
        HEAD(-1, ""), BODY(0, "small"), UPPER(0, "medium"), LOWER(1, "medium");

        final int index;
        final String folder;

        Part(int index, String folder) {
            this.index = index;
            this.folder = folder;
        }
    }

    private final BipedGolemModel<T> model;

    public BipedGolemLayer(IEntityRenderer<T, BipedGolemModel<T>> renderer, BipedGolemModel<T> model) {
        super(renderer);
        this.model = model;
    }

    @Override
    public void render(MatrixStack matrix, IRenderTypeBuffer buffer, int light, T entity, float a, float b, float c, float d, float e, float f) {
        Part[] parts = entity.isBaby() ? new Part[]{Part.BODY} : new Part[]{Part.UPPER, Part.LOWER};
        for (Part part : parts) {
            this.getParentModel().copyPropertiesTo(model);
            this.setPartVisibility(part);
            boolean foil = false;
            ResourceLocation rl = new ResourceLocation(entity.getMaterials().get(part.index));
            rl = new ResourceLocation(rl.getNamespace(), "textures/entity/alchemy_golem/" + part.folder + "/" + rl.getPath());
            IVertexBuilder ivertexbuilder = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(rl), false, foil);
            model.renderToBuffer(matrix, ivertexbuilder, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        }
    }

    protected void setPartVisibility(Part part) {
        model.setAllVisible(false);
        switch (part) {
            case HEAD:
                model.hat.visible = true;
                model.head.visible = true;
                break;
            case UPPER:
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
                break;
            case LOWER:
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
                break;
            case BODY:
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
                break;
        }
    }

}
