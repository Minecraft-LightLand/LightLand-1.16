package com.hikarishima.lightland.event.forge;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.command.ArcaneCommand;
import com.hikarishima.lightland.command.TerrainCommand;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.PlayerMagicCapability;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("unused")
public class GenericEventHandler {

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(LightLand.MODID, "magic"),
                    new PlayerMagicCapability((PlayerEntity) event.getObject(), event.getObject().level));
        }
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSource> lightland = Commands.literal("lightland");
        new ArcaneCommand(lightland);
        TerrainCommand.register(lightland);
        event.getDispatcher().register(lightland);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event){
        MagicHandler.get(event.player).tick();
    }

}
