package com.hikarishima.lightland.magic.registry.entity.golem;

import com.hikarishima.lightland.magic.capabilities.BodyAttribute;
import com.lcy0x1.core.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AlchemyGolemEntity extends GolemEntity {

    private static AttributeModifier gen(String name, double val) {
        return new AttributeModifier(BodyAttribute.getUUIDfromString(name), name, val, AttributeModifier.Operation.ADDITION);
    }

    public AlchemyGolemEntity(EntityType<? extends GolemEntity> type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes(double hp, double speed, double kb, double atk, double def) {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, hp)
                .add(Attributes.MOVEMENT_SPEED, speed)
                .add(Attributes.KNOCKBACK_RESISTANCE, kb)
                .add(Attributes.ATTACK_DAMAGE, atk)
                .add(Attributes.ARMOR, def);
    }

    @SerialClass
    public static class Material {

        @SerialClass.SerialField
        public Ingredient ingredient;
        @SerialClass.SerialField
        public double hp, speed, kb, atk, def, tough, restore, fire_reduce, thorn;
        @SerialClass.SerialField
        public int fire_tick, fire_thorn_tick;
        @SerialClass.SerialField(generic = EffEntry.class)
        public List<EffEntry> effects = new ArrayList<>();

        @SerialClass
        public static class EffEntry {

            @SerialClass.SerialField
            public Effect id;
            @SerialClass.SerialField
            public int amplifier, duration, radius;
            @SerialClass.SerialField
            public double chance;

        }

        public void onHit(AlchemyGolemEntity self) {
            for (EffEntry eff : effects) {
                if (Math.random() < eff.chance) {
                    if (eff.radius == 0) {
                        self.addEffect(new EffectInstance(eff.id, eff.duration, eff.amplifier));
                    } else {
                        self.level.getEntities(self, new AxisAlignedBB(self.blockPosition()).inflate(eff.radius))
                                .forEach(e -> {
                                    if (e instanceof LivingEntity && e.distanceTo(self) < eff.radius && !e.isAlliedTo(self)) {
                                        ((LivingEntity) e).addEffect(new EffectInstance(eff.id, eff.duration, eff.amplifier));
                                    }
                                });
                    }
                }
            }
        }

        @SuppressWarnings("ConstantConditions")
        public static void onAddAttribute(AlchemyGolemEntity self, List<Material> list) {
            double hp = 0, speed = 0, kb = 0, atk = 0, def = 0, tough = 0;
            for (Material m : list) {
                hp += m.hp;
                speed += m.speed;
                kb += m.kb;
                atk += m.atk;
                def += m.def;
                tough += m.tough;
            }
            self.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(gen("alchemy-golem.hp", hp));
            self.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(gen("alchemy-golem.speed", speed));
            self.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addPermanentModifier(gen("alchemy-golem.kb", kb));
            self.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(gen("alchemy-golem.atk", atk));
            self.getAttribute(Attributes.ARMOR).addPermanentModifier(gen("alchemy-golem.def", def));
            self.getAttribute(Attributes.ARMOR_TOUGHNESS).addPermanentModifier(gen("alchemy-golem.tough", tough));
        }

    }

}
