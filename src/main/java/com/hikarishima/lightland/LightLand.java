package com.hikarishima.lightland;

import com.hikarishima.lightland.config.FileIO;
import com.hikarishima.lightland.config.worldgen.ImageBiomeReader;
import com.hikarishima.lightland.config.road.ImageRoadReader;
import com.hikarishima.lightland.config.worldgen.VolcanoBiomeReader;
import com.hikarishima.lightland.event.ForgeEventHandlers;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.mobspawn.MobSpawn;
import com.hikarishima.lightland.proxy.ClientProxy;
import com.hikarishima.lightland.proxy.ISidedProxy;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.proxy.ServerProxy;
import com.hikarishima.lightland.registry.ItemRegistry;
import com.hikarishima.lightland.registry.RegistryBase;
import com.hikarishima.lightland.world.LightLandBiomeProvider;
import com.hikarishima.lightland.world.LightLandChunkGenerator;
import com.hikarishima.lightland.world.LightLandWorldType;
import net.minecraft.item.Item;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

// The value here should match an entry in the META-INF/mods.toml file
@SuppressWarnings("unused")
@Mod("lightland")
public class LightLand {

    public static final String MODID = "lightland";
    public static final String NETWORK_VERSION = "1";

    public static ISidedProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static LightLandWorldType WORLD_TYPE = new LightLandWorldType();

    public LightLand() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());

        proxy.init();
        PacketHandler.registerPackets();
    }

    private void setup(final FMLCommonSetupEvent event) {
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

        Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(MODID, "image_biome"), LightLandBiomeProvider.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(MODID, "lightland"), LightLandChunkGenerator.CODEC);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        MobSpawn.init();
        ImageBiomeReader.init();
        ImageRoadReader.init();
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        ((IReloadableResourceManager) event.getServer().getDataPackRegistries().getResourceManager()).registerReloadListener(this::onReload);
    }

    @SubscribeEvent
    public void onServerStart(FMLServerStartedEvent event) {
        ImageBiomeReader.genGradient();
    }

    @SubscribeEvent
    public void onServerClosing(FMLServerStoppingEvent event) {
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

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onNewRegistry(RegistryEvent.NewRegistry event){
            MagicRegistry.createRegistries();
        }

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
        public static void onItemRegistry(RegistryEvent.Register<Item> event) {
            RegistryBase.process(ItemRegistry.class, Item.class, event.getRegistry()::register);
        }

        @SubscribeEvent
        public static void onWorldTypeRegistry(RegistryEvent.Register<ForgeWorldType> event) {
            event.getRegistry().register(WORLD_TYPE.setRegistryName(MODID, "image_biome"));
        }

        @SubscribeEvent
        public static void onMagicElementRegistry(RegistryEvent.Register<MagicElement> event) {
            RegistryBase.process(MagicRegistry.class, MagicElement.class, event.getRegistry()::register);
        }

        @SubscribeEvent
        public static void onMagicProductTypeRegistry(RegistryEvent.Register<MagicRegistry.MPTRaw> event) {
            RegistryBase.process(MagicRegistry.class, MagicRegistry.MPTRaw.class, event.getRegistry()::register);
        }

    }
}
