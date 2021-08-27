package com.hikarishima.lightland.magic.registry.effect;

import com.hikarishima.lightland.magic.capabilities.BodyAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class WaterTrapEffect extends Effect {

    public static final UUID ID_FLY = BodyAttribute.getUUIDfromString("lightland-magic:water_trap.fly");
    public static final UUID ID_SWIM = BodyAttribute.getUUIDfromString("lightland-magic:water_trap.swim");
    public static final UUID ID_WALK = BodyAttribute.getUUIDfromString("lightland-magic:water_trap.walk");

    public WaterTrapEffect() {
        super(EffectType.HARMFUL, 0x7f7fff);
        this.addAttributeModifier(Attributes.FLYING_SPEED, ID_FLY.toString(), -0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ID_WALK.toString(), -0.4f, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(ForgeMod.SWIM_SPEED.get(), ID_SWIM.toString(), -0.3f, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

}
