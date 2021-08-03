package com.hikarishima.lightland.event.forge;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.command.ArcaneCommand;
import com.hikarishima.lightland.command.MagicCommand;
import com.hikarishima.lightland.command.QuestCommand;
import com.hikarishima.lightland.command.TerrainCommand;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.PlayerMagicCapability;
import com.hikarishima.lightland.magic.capabilities.ToClientMsg;
import com.hikarishima.lightland.magic.gui.overlay.ManaOverlay;
import com.hikarishima.lightland.npc.player.QuestCapability;
import com.hikarishima.lightland.npc.player.QuestHandler;
import com.hikarishima.lightland.npc.player.QuestToClient;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.proxy.Proxy;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
        new ArcaneCommand(lightland);
        new MagicCommand(lightland);
        new QuestCommand(lightland);
        TerrainCommand.register(lightland);
        event.getDispatcher().register(lightland);
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
            PacketHandler.toClient(e, new QuestToClient(QuestToClient.Action.ALL, QuestHandler.get(e)));
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayEventPre(RenderGameOverlayEvent.Pre event) {
        if (!Proxy.getClientPlayer().isAlive())
            return;
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            ManaOverlay.INSTANCE.render(event.getMatrixStack(), event.getWindow(), event.getPartialTicks());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            CompoundNBT tag0 = Automator.toTag(new CompoundNBT(), MagicHandler.get(event.getOriginal()));
            ExceptionHandler.run(() -> Automator.fromTag(tag0, MagicHandler.class, MagicHandler.get(event.getPlayer()), f -> true));
            CompoundNBT tag1 = Automator.toTag(new CompoundNBT(), QuestHandler.get(event.getOriginal()));
            ExceptionHandler.run(() -> Automator.fromTag(tag1, QuestHandler.class, QuestHandler.get(event.getPlayer()), f -> true));
            ServerPlayerEntity e = (ServerPlayerEntity) event.getPlayer();
            PacketHandler.toClient(e, new ToClientMsg(ToClientMsg.Action.ALL, MagicHandler.get(e)));
            PacketHandler.toClient(e, new QuestToClient(QuestToClient.Action.ALL, QuestHandler.get(e)));
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(ClientPlayerNetworkEvent.RespawnEvent event) {
        if (!event.getOldPlayer().isAlive()) {
            CompoundNBT tag0 = MagicHandler.getCache();
            ExceptionHandler.run(() -> Automator.fromTag(tag0, MagicHandler.class, MagicHandler.get(event.getNewPlayer()), f -> true));
            CompoundNBT tag1 = QuestHandler.getCache();
            ExceptionHandler.run(() -> Automator.fromTag(tag1, QuestHandler.class, QuestHandler.get(event.getNewPlayer()), f -> true));
        }
    }

}
