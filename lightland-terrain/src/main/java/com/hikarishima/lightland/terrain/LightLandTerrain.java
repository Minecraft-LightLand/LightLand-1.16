package com.hikarishima.lightland.terrain;

import com.hikarishima.lightland.command.BaseCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@SuppressWarnings("unused")
@Mod("lightland-terrain")
public class LightLandTerrain {

    public static final String MODID = "lightland-terrain";

    public static LightLandWorldType WORLD_TYPE = new LightLandWorldType();

    public LightLandTerrain() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(new WorldGenEventHandler());
        BaseCommand.LIST.add(TerrainCommand::register);
    }

    private void setup(final FMLCommonSetupEvent event) {
        WorldGenEventHandler.mod_setup();
    }

}
