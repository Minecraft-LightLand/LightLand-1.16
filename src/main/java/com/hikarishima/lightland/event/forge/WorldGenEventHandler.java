package com.hikarishima.lightland.event.forge;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.config.FileIO;
import com.hikarishima.lightland.config.road.ImageRoadReader;
import com.hikarishima.lightland.config.worldgen.ImageBiomeReader;
import com.hikarishima.lightland.config.worldgen.VolcanoBiomeReader;
import com.hikarishima.lightland.world.LightLandBiomeProvider;
import com.hikarishima.lightland.world.LightLandChunkGenerator;
import com.hikarishima.lightland.world.mobspawn.MobSpawn;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SuppressWarnings("unused")
public class WorldGenEventHandler {

    public static void mod_setup() {
        FileIO.loadConfigFile("biome.png");
        FileIO.loadConfigFile("biome_config.json");
        FileIO.loadConfigFile("road.png");
        FileIO.loadConfigFile("road_config.json");
        FileIO.loadConfigFile("volcano_config.json");
        FileIO.loadConfigFile("spawn_rules.json");
        FileIO.loadConfigFile("item_cost.json");
        FileIO.loadConfigFile("enchant_cost.json");
        FileIO.loadConfigFile("potion_cost.json");
        FileIO.loadConfigFile("buff_cost.json");

        VolcanoBiomeReader.init();

        Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(LightLand.MODID, "image_biome"), LightLandBiomeProvider.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(LightLand.MODID, "lightland"), LightLandChunkGenerator.CODEC);

    }

    @SubscribeEvent
    public void onPotentialSpawns(WorldEvent.PotentialSpawns event) {
        IWorld world = event.getWorld();
        if(!(world instanceof ServerWorld && ((ServerWorld)world).getChunkSource().generator instanceof LightLandChunkGenerator))
            return;
        EntityClassification cls = event.getType();
        if (cls == EntityClassification.MONSTER) {
            List<MobSpawnInfo.Spawners> list = event.getList();
            list.clear();
            MobSpawn.fillSpawnList(world, list, event.getPos());
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent
    public void doSpecialSpawns(LivingSpawnEvent.SpecialSpawn event) {
        IWorld world = event.getWorld();
        if(!(world instanceof ServerWorld && ((ServerWorld)world).getChunkSource().generator instanceof LightLandChunkGenerator))
            return;
        LivingEntity ent = event.getEntityLiving();
        if (!MobSpawn.modifySpawnedEntity(world, ent))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onBiomeLoading(BiomeLoadingEvent event) {
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

    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        MobSpawn.init();
        ImageBiomeReader.init();
        ImageRoadReader.init();
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        if(!(event.getServer().overworld().getChunkSource().generator instanceof LightLandChunkGenerator))
            return;
        ((IReloadableResourceManager) event.getServer().getDataPackRegistries().getResourceManager()).registerReloadListener(this::onReload);
    }

    @SubscribeEvent
    public void onServerStart(FMLServerStartedEvent event) {
        if(!(event.getServer().overworld().getChunkSource().generator instanceof LightLandChunkGenerator))
            return;
        ImageBiomeReader.genGradient();
    }

    @SubscribeEvent
    public void onServerClosing(FMLServerStoppingEvent event) {
        if(!(event.getServer().overworld().getChunkSource().generator instanceof LightLandChunkGenerator))
            return;
        ImageBiomeReader.clear();
        ImageRoadReader.clear();
    }

    private CompletableFuture<Void> onReload(IFutureReloadListener.IStage stage, IResourceManager manager, IProfiler p0, IProfiler p1, Executor e0, Executor e1) {
        return CompletableFuture.runAsync(() -> {
            MobSpawn.init();
            ImageBiomeReader.init();
            ImageRoadReader.init();
        });
    }

}
