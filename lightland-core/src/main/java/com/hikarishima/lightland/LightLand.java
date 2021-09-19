package com.hikarishima.lightland;

import com.hikarishima.lightland.command.EnumParser;
import com.hikarishima.lightland.event.forge.DamageEventHandler;
import com.hikarishima.lightland.event.forge.GenericEventHandler;
import com.hikarishima.lightland.event.forge.ItemUseEventHandler;
import com.hikarishima.lightland.event.forge.PermissionEventHandler;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.recipe.RecipeRegistry;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;

// The value here should match an entry in the META-INF/mods.toml file
@SuppressWarnings("unused")
@Mod("lightland-core")
@Log4j2
public class LightLand {

    public static final String MODID = "lightland-core";
    public static final String NETWORK_VERSION = "0.4.17";

    public LightLand() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(new ItemUseEventHandler());
        MinecraftForge.EVENT_BUS.register(new GenericEventHandler());
        MinecraftForge.EVENT_BUS.register(new DamageEventHandler());
        MinecraftForge.EVENT_BUS.register(new PermissionEventHandler());
        PacketHandler.reg(ItemUseEventHandler.Msg.class, ItemUseEventHandler.Msg::handle, NetworkDirection.PLAY_TO_SERVER);
        RecipeRegistry.REC.register(bus);
        EnumParser.register();
    }

}
