package com.hikarishima.lightland.terrain;

import com.lcy0x1.core.util.ExceptionHandler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

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
            process(cls, event.getRegistry().getRegistrySuperType(), event.getRegistry()::register);
    }


    @SubscribeEvent
    public static void onWorldTypeRegistry(RegistryEvent.Register<ForgeWorldType> event) {
        event.getRegistry().register(LightLandTerrain.WORLD_TYPE.setRegistryName(LightLandTerrain.MODID, "image_biome"));
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> void process(Class<?> provider, Class<T> reg, Consumer<T> acceptor) {
        ExceptionHandler.run(() -> {
            for (Field f : provider.getDeclaredFields())
                if ((f.getModifiers() & Modifier.STATIC) != 0)
                    if (reg.isAssignableFrom(f.getType()))
                        ((Consumer) acceptor).accept(f.get(null));
                    else if (f.getType().isArray() && reg.isAssignableFrom(f.getType().getComponentType()))
                        for (Object o : (Object[]) f.get(null))
                            ((Consumer) acceptor).accept(o);
        });
    }

}
