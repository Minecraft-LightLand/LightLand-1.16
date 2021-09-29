package com.hikarishima.lightland.magic.registry.entity.golem;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@SerialClass
@ParametersAreNonnullByDefault
public class LargeAlchemyGolemEntity extends AlchemyGolemEntity {

    private int attackAnimationTick;

    public LargeAlchemyGolemEntity(EntityType<? extends LargeAlchemyGolemEntity> type, World world) {
        super(type, world);
    }

    public boolean doHurtTarget(Entity entity) {
        this.attackAnimationTick = 10;
        this.level.broadcastEntityEvent(this, (byte) 4);
        float f = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        boolean flag = entity.hurt(getDamageSource(), f);
        if (flag) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.4F, 0.0D));
            this.doEnchantDamageEffects(this, entity);
            int tick = getMerged().fire_tick;
            if (tick > 0) {
                entity.setSecondsOnFire(tick / 20);
            }
        }
        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    public int getAttackAnimationTick() {
        return attackAnimationTick;
    }
}
