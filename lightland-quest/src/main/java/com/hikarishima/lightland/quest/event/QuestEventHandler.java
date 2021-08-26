package com.hikarishima.lightland.quest.event;

import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.quest.LightLandQuest;
import com.hikarishima.lightland.quest.gui.GUIDispatcher;
import com.hikarishima.lightland.quest.player.QuestCapability;
import com.hikarishima.lightland.quest.player.QuestHandler;
import com.hikarishima.lightland.quest.player.QuestToClient;
import com.hikarishima.lightland.quest.token.LocationVisitToken;
import com.hikarishima.lightland.quest.token.MobKillToken;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collection;

@SuppressWarnings("unused")
public class QuestEventHandler {

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(LightLandQuest.MODID, "quest"),
                    new QuestCapability((PlayerEntity) event.getObject(), event.getObject().level));
        }
    }

    @SubscribeEvent
    public void onServerPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity e = (ServerPlayerEntity) event.getPlayer();
        if (e != null) {
            PacketHandler.toClient(e, new QuestToClient(QuestToClient.Action.ALL, QuestHandler.get(e)));
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        CompoundNBT tag1 = Automator.toTag(new CompoundNBT(), QuestHandler.get(event.getOriginal()));
        ExceptionHandler.run(() -> Automator.fromTag(tag1, QuestHandler.class, QuestHandler.get(event.getPlayer()), f -> true));
        ServerPlayerEntity e = (ServerPlayerEntity) event.getPlayer();
        PacketHandler.toClient(e, new QuestToClient(QuestToClient.Action.CLONE, QuestHandler.get(e)));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onPlayerRespawn(ClientPlayerNetworkEvent.RespawnEvent event) {
        CompoundNBT tag1 = QuestHandler.getCache(event.getOldPlayer());
        ExceptionHandler.run(() -> Automator.fromTag(tag1, QuestHandler.class, QuestHandler.get(event.getNewPlayer()), f -> true));
    }

    @SubscribeEvent
    public void onLivingKillEvent(LivingDeathEvent event) {
        Entity e0 = event.getSource().getDirectEntity();
        Entity e1 = event.getSource().getEntity();
        PlayerEntity le = null;
        if (e0 instanceof PlayerEntity)
            le = (PlayerEntity) e0;
        else if (e1 instanceof PlayerEntity)
            le = (PlayerEntity) e1;
        if (le != null) {
            QuestHandler handler = QuestHandler.get(le);
            LivingEntity e = event.getEntityLiving();
            Collection<MobKillToken> list = handler.getTokens(MobKillToken.class);
            for (MobKillToken token : list) {
                token.onKill(e);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.isAlive()) {
            QuestHandler parent = QuestHandler.get(event.player);
            Collection<LocationVisitToken> list = parent.getTokens(LocationVisitToken.class);
            for (LocationVisitToken token : list) {
                token.visit(event.player.getPosition(0));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (GUIDispatcher.onClick(event.getPlayer(), event.getTarget())) {
            event.setCanceled(true);
            event.setCancellationResult(ActionResultType.SUCCESS);
        }
    }


}
