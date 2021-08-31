package com.hikarishima.lightland.terrain;

import com.hikarishima.lightland.config.FileIO;
import com.hikarishima.lightland.terrain.config.ImageBiomeReader;
import com.hikarishima.lightland.terrain.config.ImageRoadReader;
import com.hikarishima.lightland.terrain.config.VolcanoBiomeReader;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SuppressWarnings("unused")
public class WorldGenEventHandler {

    public static void mod_setup() {
        FileIO.loadConfigFile(LightLandTerrain.MODID,"biome.png");
        FileIO.loadConfigFile(LightLandTerrain.MODID,"biome_config.json");
        FileIO.loadConfigFile(LightLandTerrain.MODID,"road.png");
        FileIO.loadConfigFile(LightLandTerrain.MODID,"road_config.json");
        FileIO.loadConfigFile(LightLandTerrain.MODID,"volcano_config.json");
        FileIO.loadConfigFile(LightLandTerrain.MODID,"spawn_rules.json");
        FileIO.loadConfigFile(LightLandTerrain.MODID,"item_cost.json");
        FileIO.loadConfigFile(LightLandTerrain.MODID,"enchant_cost.json");
        FileIO.loadConfigFile(LightLandTerrain.MODID,"potion_cost.json");
        FileIO.loadConfigFile(LightLandTerrain.MODID,"buff_cost.json");

        VolcanoBiomeReader.init();

        Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(LightLandTerrain.MODID, "image_biome"), LightLandBiomeProvider.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(LightLandTerrain.MODID, "lightland"), LightLandChunkGenerator.CODEC);

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
    }

    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        ImageBiomeReader.init();
        ImageRoadReader.init();
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        if (!(event.getServer().overworld().getChunkSource().generator instanceof LightLandChunkGenerator))
            return;
        ((IReloadableResourceManager) event.getServer().getDataPackRegistries().getResourceManager()).registerReloadListener(this::onReload);
    }

    @SubscribeEvent
    public void onServerStart(FMLServerStartedEvent event) {
        if (!(event.getServer().overworld().getChunkSource().generator instanceof LightLandChunkGenerator))
            return;
        ImageBiomeReader.genGradient();
    }

    @SubscribeEvent
    public void onServerClosing(FMLServerStoppingEvent event) {
        if (!(event.getServer().overworld().getChunkSource().generator instanceof LightLandChunkGenerator))
            return;
        ImageBiomeReader.clear();
        ImageRoadReader.clear();
    }

    private CompletableFuture<Void> onReload(IFutureReloadListener.IStage stage, IResourceManager manager, IProfiler p0, IProfiler p1, Executor e0, Executor e1) {
        return CompletableFuture.runAsync(() -> {
            ImageBiomeReader.init();
            ImageRoadReader.init();
        });
    }

}
