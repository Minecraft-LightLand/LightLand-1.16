package com.hikarishima.lightland.magic.event;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.PlayerMagicCapability;
import com.hikarishima.lightland.magic.capabilities.ToClientMsg;
import com.hikarishima.lightland.magic.gui.overlay.ManaOverlay;
import com.hikarishima.lightland.magic.gui.overlay.WandOverlay;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.proxy.Proxy;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("unused")
public class MagicEventHandler {

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(LightLandMagic.MODID, "magic"),
                    new PlayerMagicCapability((PlayerEntity) event.getObject(), event.getObject().level));
        }
    }


    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.isAlive())
            MagicHandler.get(event.player).tick();
    }

    @SubscribeEvent
    public void onServerPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity e = (ServerPlayerEntity) event.getPlayer();
        if (e != null) {
            PacketHandler.toClient(e, new ToClientMsg(ToClientMsg.Action.ALL, MagicHandler.get(e)));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void keyEvent(InputEvent.KeyInputEvent event) {
        if (Minecraft.getInstance().screen == null && Proxy.getClientPlayer() != null && WandOverlay.has_magic_wand) {
            WandOverlay.input(event.getKey(), event.getAction());
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlayEventPre(RenderGameOverlayEvent.Pre event) {
        if (!Proxy.getClientPlayer().isAlive())
            return;
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            if (ManaOverlay.INSTANCE.render(event.getMatrixStack(), event.getWindow(), event.getPartialTicks()))
                event.setCanceled(true);
        }
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            if (WandOverlay.INSTANCE.render(event.getMatrixStack(), event.getWindow(), event.getPartialTicks()))
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        CompoundNBT tag0 = Automator.toTag(new CompoundNBT(), MagicHandler.get(event.getOriginal()));
        ExceptionHandler.run(() -> Automator.fromTag(tag0, MagicHandler.class, MagicHandler.get(event.getPlayer()), f -> true));
        MagicHandler.get(event.getPlayer());
        ServerPlayerEntity e = (ServerPlayerEntity) event.getPlayer();
        PacketHandler.toClient(e, new ToClientMsg(ToClientMsg.Action.CLONE, MagicHandler.get(e)));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onPlayerRespawn(ClientPlayerNetworkEvent.RespawnEvent event) {
        CompoundNBT tag0 = MagicHandler.getCache(event.getOldPlayer());
        ExceptionHandler.run(() -> Automator.fromTag(tag0, MagicHandler.class, MagicHandler.get(event.getNewPlayer()), f -> true));
        MagicHandler.get(event.getNewPlayer());
    }

}
