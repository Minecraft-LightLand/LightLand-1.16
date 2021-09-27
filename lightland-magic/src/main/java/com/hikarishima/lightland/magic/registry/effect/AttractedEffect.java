package com.hikarishima.lightland.magic.registry.effect;

import com.hikarishima.lightland.magic.registry.VanillaMagicRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.math.AxisAlignedBB;

public class AttractedEffect extends Effect {

    public AttractedEffect() {
        super(EffectType.NEUTRAL, 0xFFFFFF);
    }

    public void applyEffectTick(LivingEntity self, int level) {
        if (self.level.isClientSide() || !(self instanceof MobEntity))
            return;
        int radius = 16 + level * 4;
        double dist = radius;
        LivingEntity ans = null;
        for (Entity e : self.level.getEntities(self, new AxisAlignedBB(self.blockPosition()).inflate(radius))) {
            double d = e.distanceTo(self);
            if (d < dist && e instanceof LivingEntity && !e.isAlliedTo(self) && ((LivingEntity) e).getEffect(VanillaMagicRegistry.EFF_ATTRACT.get()) != null) {
                ans = (LivingEntity) e;
                dist = d;
            }
        }
        if (ans != null) {
            ((MobEntity) self).setTarget(ans);
        }
    }

    public boolean isDurationEffectTick(int tick, int lv) {
        return tick % 10 == 0;
    }

}
