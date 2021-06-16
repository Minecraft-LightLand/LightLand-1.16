package com.hikarishima.lightland;

import com.hikarishima.lightland.mobspawn.MobSpawn;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ForgeEventHandlers {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPotentialSpawns(WorldEvent.PotentialSpawns event) {
        EntityClassification cls = event.getType();
        if (cls == EntityClassification.MONSTER) {
            List<MobSpawnInfo.Spawners> list = event.getList();
            list.clear();
            MobSpawn.spawn(list, event.getPos());
        }

    }

}
