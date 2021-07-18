package com.hikarishima.lightland.event.registry;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.registry.RegistryBase;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@SuppressWarnings("unused")
public class WorldGenRegistryEvents {

    @SubscribeEvent
    public static void onPlacementRegistry(RegistryEvent.Register<Placement<?>> event) {
        RegistryBase.processBiome(event);
    }

    @SubscribeEvent
    public static void onFeatureRegistry(RegistryEvent.Register<Feature<?>> event) {
        RegistryBase.processBiome(event);
    }

    @SubscribeEvent
    public static void onSurfaceBuilderRegistry(RegistryEvent.Register<SurfaceBuilder<?>> event) {
        RegistryBase.processBiome(event);
    }

    @SubscribeEvent
    public static void onBiomeRegistry(RegistryEvent.Register<Biome> event) {
        RegistryBase.processBiome(event);
    }

    @SubscribeEvent
    public static void onWorldTypeRegistry(RegistryEvent.Register<ForgeWorldType> event) {
        event.getRegistry().register(LightLand.WORLD_TYPE.setRegistryName(LightLand.MODID, "image_biome"));
    }

}
