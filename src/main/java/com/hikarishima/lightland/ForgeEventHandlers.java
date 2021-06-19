package com.hikarishima.lightland;

import com.hikarishima.lightland.mobspawn.MobSpawn;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ForgeEventHandlers {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPotentialSpawns(WorldEvent.PotentialSpawns event) {
        EntityClassification cls = event.getType();
        if (cls == EntityClassification.MONSTER) {
            IWorld world = event.getWorld();
            List<MobSpawnInfo.Spawners> list = event.getList();
            list.clear();
            MobSpawn.fillSpawnList(world, list, event.getPos());
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void doSpecialSpawns(LivingSpawnEvent.SpecialSpawn event) {
        IWorld world = event.getWorld();
        LivingEntity ent = event.getEntityLiving();
        if(!MobSpawn.modifySpawnedEntity(world, ent))
            event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBiomeLoading(BiomeLoadingEvent event){
        event.getGeneration().getCarvers(GenerationStage.Carving.AIR).clear();
        event.getGeneration().getCarvers(GenerationStage.Carving.LIQUID).clear();
        event.getGeneration().getStructures().clear();
        event.getGeneration().getFeatures(GenerationStage.Decoration.STRONGHOLDS).clear();
        event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).clear();
        event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_STRUCTURES).clear();
        event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_DECORATION).clear();
        event.getGeneration().getFeatures(GenerationStage.Decoration.LAKES).clear();
        List<MobSpawnInfo.Spawners> list = event.getSpawns().getSpawner(EntityClassification.MONSTER);
        list.clear();
        MobSpawn.addAllSpawns(list);

    }

}
