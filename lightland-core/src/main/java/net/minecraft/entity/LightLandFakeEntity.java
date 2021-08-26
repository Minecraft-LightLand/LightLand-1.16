package net.minecraft.entity;

import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;

public class LightLandFakeEntity {

    public static void actuallyHurt(LivingEntity e, DamageSource source, float damage) {
        e.actuallyHurt(source, damage);
    }

    public static float getDamageAfterArmorAbsorb(LivingEntity e, DamageSource source, float damage) {
        return e.getDamageAfterArmorAbsorb(source, damage);
    }

    public static float getDamageAfterMagicAbsorb(LivingEntity e, DamageSource source, float damage) {
        return e.getDamageAfterMagicAbsorb(source, damage);
    }

    public static void hurtArmor(LivingEntity e, DamageSource source, float damage) {
        e.hurtArmor(source, damage);
    }

    /**
     * force add effect, make boss not override
     * for icon use only, such as Arcane Mark on Wither and Ender Dragon
     */
    public static void addEffect(LivingEntity e, EffectInstance ins) {
        EffectInstance effectinstance = e.getActiveEffectsMap().get(ins.getEffect());
        MinecraftForge.EVENT_BUS.post(new PotionEvent.PotionAddedEvent(e, effectinstance, ins));
        if (effectinstance == null) {
            e.getActiveEffectsMap().put(ins.getEffect(), ins);
            e.onEffectAdded(ins);
        } else if (effectinstance.update(ins)) {
            e.onEffectUpdated(effectinstance, true);
        }
    }

}
