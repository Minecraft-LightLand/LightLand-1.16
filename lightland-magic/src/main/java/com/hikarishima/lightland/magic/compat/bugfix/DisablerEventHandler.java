package com.hikarishima.lightland.magic.compat.bugfix;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.item.Items;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("unused")
public class DisablerEventHandler {

    @SubscribeEvent
    public void onEntityDrop(LivingDropsEvent event) {
        LivingEntity le = event.getEntityLiving();
        if (le instanceof IronGolemEntity) {
            if (!((IronGolemEntity) le).isPlayerCreated()) {
                event.getDrops().removeIf(e -> e.getItem().getItem() == Items.IRON_INGOT);
            }
        }
    }

}
