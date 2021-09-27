package com.hikarishima.lightland.magic.registry.entity.golem.render;

import com.hikarishima.lightland.magic.registry.entity.golem.AlchemyGolemEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BipedGolemModel<T extends AlchemyGolemEntity> extends BipedModel<T> {
    protected BipedGolemModel(float a, boolean b) {
        super(a, 0.0F, 64, b ? 32 : 64);
    }

    public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
        super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
        ModelHelper.animateZombieArms(this.leftArm, this.rightArm, this.isAggressive(p_225597_1_), this.attackTime, p_225597_4_);
    }

    public boolean isAggressive(T p_212850_1_) {
        return p_212850_1_.isAggressive();
    }
}
