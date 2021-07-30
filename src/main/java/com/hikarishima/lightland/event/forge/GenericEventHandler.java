package com.hikarishima.lightland.event.forge;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.command.MagicCommand;
import com.hikarishima.lightland.command.TerrainCommand;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.PlayerMagicCapability;
import com.hikarishima.lightland.magic.capabilities.ToClientMsg;
import com.hikarishima.lightland.npc.player.QuestCapability;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("unused")
public class GenericEventHandler {

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(LightLand.MODID, "magic"),
                    new PlayerMagicCapability((PlayerEntity) event.getObject(), event.getObject().level));
            event.addCapability(new ResourceLocation(LightLand.MODID, "quest"),
                    new QuestCapability((PlayerEntity) event.getObject(), event.getObject().level));
        }
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSource> lightland = Commands.literal("lightland");
        new MagicCommand(lightland);
        TerrainCommand.register(lightland);
        event.getDispatcher().register(lightland);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        MagicHandler.get(event.player).tick();
    }

    @SubscribeEvent
    public void onServerPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity e = (ServerPlayerEntity) event.getPlayer();
        if (e != null) {
            PacketHandler.toClient(e, new ToClientMsg(ToClientMsg.Action.ALL, MagicHandler.get(e)));
            //TODO quest packer
        }
    }

}
