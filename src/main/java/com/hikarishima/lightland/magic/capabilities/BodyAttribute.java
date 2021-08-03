package com.hikarishima.lightland.magic.capabilities;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

public class BodyAttribute {

    private static double sq(double v) {
        return v * (v + 1) / 2d;
    }

    private static double exp(double e, double v) {
        return Math.pow(e, v) - 1;
    }

    private static UUID getUUIDfromString(String str) {
        int hash = str.hashCode();
        Random r = new Random(hash);
        long l0 = r.nextLong();
        long l1 = r.nextLong();
        return new UUID(l0, l1);
    }

    public static void resetModifiers(AbilityPoints ability, PlayerEntity player) {
        for (Attr attr : Attr.values()) {
            ModifiableAttributeInstance ins = player.getAttribute(attr.attr);
            if (ins != null) {
                ins.removeModifier(attr.id);
                ins.addPermanentModifier(attr.get(ability));
            }
        }
    }

    public enum Attr {
        HEALTH("lightland.health", Attributes.MAX_HEALTH,
                AttributeModifier.Operation.ADDITION, a -> sq(a.health) * 2),
        ARMOR("lightland.armor", Attributes.ARMOR,
                AttributeModifier.Operation.ADDITION, a -> sq(a.health)),
        TOUGH("lightland.toughness", Attributes.ARMOR_TOUGHNESS,
                AttributeModifier.Operation.ADDITION, a -> sq(a.health)),
        DAMAGE("lightland.damage", Attributes.ATTACK_DAMAGE,
                AttributeModifier.Operation.MULTIPLY_TOTAL, a -> exp(1.2, a.strength)),
        ATK_SPEED("lightland.atk_speed", Attributes.ATTACK_SPEED,
                AttributeModifier.Operation.MULTIPLY_TOTAL, a -> exp(1.05, a.strength) + exp(1.05, a.speed)),
        MOVE_SPEED("lightland.move_speed", Attributes.MOVEMENT_SPEED,
                AttributeModifier.Operation.MULTIPLY_TOTAL, a -> exp(1.1, a.speed)),
        FLY_SPEED("lightland.fly_speed", Attributes.FLYING_SPEED,
                AttributeModifier.Operation.MULTIPLY_TOTAL, a -> exp(1.1, a.speed));

        public final String name;
        public final UUID id;
        public final Attribute attr;
        public final AttributeModifier.Operation op;
        public final Function<AbilityPoints, Double> getter;

        Attr(String name, Attribute attr, AttributeModifier.Operation op, Function<AbilityPoints, Double> getter) {
            this.name = name;
            this.id = getUUIDfromString(name);
            this.attr = attr;
            this.op = op;
            this.getter = getter;
        }

        public AttributeModifier get(AbilityPoints ability) {
            return new AttributeModifier(id, name, getter.apply(ability), AttributeModifier.Operation.ADDITION);
        }

    }

}
