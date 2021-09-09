package com.hikarishima.lightland.magic.registry.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class EmeraldPopeEffect extends Effect {

    public static final int RADIUS = 10;

    public EmeraldPopeEffect() {
        super(EffectType.NEUTRAL, 0x00FF00);
    }

    public void applyEffectTick(LivingEntity self, int level) {
        if (self.level.isClientSide())
            return;
        int radius = (level + 1) * RADIUS;
        int damage = (level + 1) * 10;
        DamageSource source = new IndirectEntityDamageSource("emerald", null, self);
        for (Entity e : self.level.getEntities(self, new AxisAlignedBB(self.blockPosition()).inflate(radius))) {
            if (e instanceof LivingEntity && !e.isAlliedTo(self) && ((LivingEntity) e).hurtTime == 0 &&
                    e.position().distanceToSqr(self.position()) < radius * radius) {
                double dist = e.position().distanceTo(self.position());
                if (dist > 0.1) {
                    ((LivingEntity) e).knockback(0.4F, e.position().x - self.position().x, e.position().z - self.position().z);
                }
                e.hurt(source, damage);
            }
        }
    }

    public boolean isDurationEffectTick(int tick, int lv) {
        return tick % 10 == 0;
    }

}
