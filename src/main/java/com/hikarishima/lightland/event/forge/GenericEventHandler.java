package com.hikarishima.lightland.event.forge;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.command.ArcaneCommand;
import com.hikarishima.lightland.magic.capabilities.PlayerMagicCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GenericEventHandler {

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(LightLand.MODID, "magic"),
                    new PlayerMagicCapability(event.getObject().level));
        }
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        ArcaneCommand.register(event.getDispatcher());
    }

}
