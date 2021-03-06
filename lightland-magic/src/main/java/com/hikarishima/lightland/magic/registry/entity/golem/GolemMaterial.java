package com.hikarishima.lightland.magic.registry.entity.golem;

import com.hikarishima.lightland.magic.capabilities.BodyAttribute;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;

@SerialClass
public class GolemMaterial {

    private static AttributeModifier gen(String name, double val) {
        return new AttributeModifier(BodyAttribute.getUUIDfromString(name), name, val, AttributeModifier.Operation.ADDITION);
    }

    @SerialClass.SerialField
    public Ingredient ingredient;
    @SerialClass.SerialField
    public double hp, speed, kb, atk, def, tough;
    @SerialClass.SerialField
    public double restore, fire_reduce, thorn, bypass_armor, bypass_magic;
    @SerialClass.SerialField
    public int fire_tick, fire_thorn_tick;
    @SerialClass.SerialField(generic = EffEntry.class)
    public ArrayList<EffEntry> effects = new ArrayList<>();

    public GolemMaterial() {

    }

    public GolemMaterial(List<GolemMaterial> list) {
        for (GolemMaterial mat : list) {
            hp += mat.hp;
            speed += mat.speed;
            kb += mat.kb;
            atk += mat.atk;
            def += mat.def;
            tough += mat.tough;
            restore += mat.restore;
            fire_reduce += mat.fire_reduce;
            thorn += mat.thorn;
            bypass_armor += mat.bypass_armor;
            bypass_magic += mat.bypass_magic;
            fire_tick += mat.fire_tick;
            fire_thorn_tick += mat.fire_thorn_tick;
            effects.addAll(mat.effects);
        }
        kb = Math.min(1, kb);
        fire_reduce = Math.min(1, fire_reduce);
        bypass_armor = Math.min(1, bypass_armor);
        bypass_magic = Math.min(1, bypass_magic);
    }

    @SerialClass
    public static class EffEntry {

        @SerialClass.SerialField
        public Effect id;
        @SerialClass.SerialField
        public int amplifier, duration, radius;
        @SerialClass.SerialField
        public double chance;

    }

    public void onHit(AlchemyGolemEntity self, LivingEntity attacker, DamageSource source, float amount) {
        if (attacker != null) {
            if (thorn > 0) {
                attacker.hurt(DamageSource.thorns(self), (float) (amount * thorn));
            }
            if (fire_thorn_tick > 0) {
                attacker.setSecondsOnFire(fire_thorn_tick / 20);
            }
        }
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
    public static void onAddAttribute(AlchemyGolemEntity self, List<GolemMaterial> list) {
        double hp = 0, speed = 0, kb = 0, atk = 0, def = 0, tough = 0;
        for (GolemMaterial m : list) {
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
