package com.hikarishima.lightland.magic.registry.effect;

import com.hikarishima.lightland.magic.capabilities.BodyAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class WaterTrapEffect extends Effect {

    public static final UUID ID = BodyAttribute.getUUIDfromString("lightland-magic:water_trap");

    public WaterTrapEffect() {
        super(EffectType.HARMFUL, 0x7f7fff);
        this.addAttributeModifier(Attributes.FLYING_SPEED, ID.toString(), -0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.JUMP_STRENGTH, ID.toString(), -0.4f, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ID.toString(), -0.4f, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(ForgeMod.SWIM_SPEED.get(), ID.toString(), -0.3f, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

}
