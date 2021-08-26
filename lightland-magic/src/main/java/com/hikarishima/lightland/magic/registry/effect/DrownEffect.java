package com.hikarishima.lightland.magic.registry.effect;

import com.hikarishima.lightland.magic.capabilities.BodyAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class DrownEffect extends Effect {

    public static final UUID ID = BodyAttribute.getUUIDfromString("lightland-magic:drown");

    public DrownEffect() {
        super(EffectType.HARMFUL, 0x00007F);
        this.addAttributeModifier(ForgeMod.SWIM_SPEED.get(), ID.toString(), -0.2f, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

}
