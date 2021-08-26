package com.hikarishima.lightland.terrain;

import com.hikarishima.lightland.registry.RegistryBase;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@SuppressWarnings("unused")
public class WorldGenRegistryEvents {

    public static final Class<?>[] BIOME_REGISTRIES = {VolcanoBiomeRegistry.class};

    @SubscribeEvent
    public static void onPlacementRegistry(RegistryEvent.Register<Placement<?>> event) {
        processBiome(event);
    }

    @SubscribeEvent
    public static void onFeatureRegistry(RegistryEvent.Register<Feature<?>> event) {
        processBiome(event);
    }

    @SubscribeEvent
    public static void onSurfaceBuilderRegistry(RegistryEvent.Register<SurfaceBuilder<?>> event) {
        processBiome(event);
    }

    @SubscribeEvent
    public static void onBiomeRegistry(RegistryEvent.Register<Biome> event) {
        processBiome(event);
    }

    public static <T extends IForgeRegistryEntry<T>> void processBiome(RegistryEvent.Register<T> event) {
        for (Class<?> cls : BIOME_REGISTRIES)
            RegistryBase.process(cls, event.getRegistry().getRegistrySuperType(), event.getRegistry()::register);
    }


    @SubscribeEvent
    public static void onWorldTypeRegistry(RegistryEvent.Register<ForgeWorldType> event) {
        event.getRegistry().register(LightLandTerrain.WORLD_TYPE.setRegistryName(LightLandTerrain.MODID, "image_biome"));
    }

}
