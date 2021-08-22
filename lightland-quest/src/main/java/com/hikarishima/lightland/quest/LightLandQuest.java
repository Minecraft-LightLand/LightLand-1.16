package com.hikarishima.lightland.quest;

import com.hikarishima.lightland.command.BaseCommand;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.quest.command.QuestCommand;
import com.hikarishima.lightland.quest.event.QuestEventHandler;
import com.hikarishima.lightland.quest.option.OptionToClient;
import com.hikarishima.lightland.quest.option.OptionToServer;
import com.hikarishima.lightland.quest.player.QuestHandler;
import com.hikarishima.lightland.quest.player.QuestToClient;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;

// The value here should match an entry in the META-INF/mods.toml file
@SuppressWarnings("unused")
@Mod("lightland-quest")
@Log4j2
public class LightLandQuest {

    public static final String MODID = "lightland-quest";

    public LightLandQuest() {
        log.debug("loading {}", MODID);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(new QuestEventHandler());
        PacketHandler.reg(OptionToClient.class, OptionToClient::handle, NetworkDirection.PLAY_TO_CLIENT);
        PacketHandler.reg(QuestToClient.class, QuestToClient::handle, NetworkDirection.PLAY_TO_CLIENT);
        PacketHandler.reg(OptionToServer.class, OptionToServer::handle, NetworkDirection.PLAY_TO_SERVER);
        BaseCommand.LIST.add(QuestCommand::new);
    }

    private void setup(final FMLCommonSetupEvent event) {
        QuestHandler.register();
    }

}
