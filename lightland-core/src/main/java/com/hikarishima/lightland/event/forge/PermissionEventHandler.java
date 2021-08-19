package com.hikarishima.lightland.event.forge;

import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("unused")
public class PermissionEventHandler {

    @SubscribeEvent
    public void onBlockBreakEvent(BlockEvent.BreakEvent event) {

    }

    @SubscribeEvent
    public void onBlockToolInteractEvent(BlockEvent.BlockToolInteractEvent event) {

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {

    }

    @SubscribeEvent
    public void onPlayerOpenContainer(PlayerContainerEvent.Open event) {

    }

    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {

    }

    @SubscribeEvent
    public void onMobGriefingEvent(EntityMobGriefingEvent event){
        
    }

}
