package com.hikarishima.lightland.magic.event;

import com.hikarishima.lightland.magic.arcane.ArcaneRegistry;
import com.hikarishima.lightland.magic.registry.VanillaMagicRegistry;
import net.minecraft.entity.LightLandFakeEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("unused")
public class MagicDamageEventHandler {

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.getSource().getDirectEntity() instanceof LightningBoltEntity) {
            if (event.getSource().getEntity() instanceof PlayerEntity) {
                if (event.getEntityLiving().isAlliedTo(event.getSource().getEntity())) {
                    event.setCanceled(true);
                    return;
                }
                LightLandFakeEntity.addEffect(event.getEntityLiving(), new EffectInstance(VanillaMagicRegistry.EFF_ARCANE, ArcaneRegistry.ARCANE_TIME));
            }
        }
    }

}
