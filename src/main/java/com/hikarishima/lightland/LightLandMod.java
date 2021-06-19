package com.hikarishima.lightland;

import com.hikarishima.lightland.mobspawn.MobSpawn;
import com.hikarishima.lightland.world.ImageBiomeReader;
import com.hikarishima.lightland.world.LightLandBiomeProvider;
import com.hikarishima.lightland.world.LightLandWorldType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("lightland")
public class LightLandMod {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static LightLandWorldType WORLD_TYPE= new LightLandWorldType();

    public LightLandMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
    }

    private void setup(final FMLCommonSetupEvent event) {
        Registry.register(Registry.BIOME_SOURCE, new ResourceLocation("lightland","image_biome"),LightLandBiomeProvider.CODEC);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event){
        ImageBiomeReader.init();
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        MobSpawn.init();
        ((IReloadableResourceManager) event.getServer().getDataPackRegistries().getResourceManager()).registerReloadListener(this::onReload);
    }

    private CompletableFuture<Void> onReload(IFutureReloadListener.IStage stage, IResourceManager manager, IProfiler p0, IProfiler p1, Executor e0, Executor e1) {
        return CompletableFuture.runAsync(() -> {
            MobSpawn.init();
        });
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onWorldTypeRegistry(RegistryEvent.Register<ForgeWorldType> event){
            event.getRegistry().register(WORLD_TYPE.setRegistryName("lightland","custom_biome"));
        }

    }
}
