package com.hikarishima.lightland.event.forge;

import com.hikarishima.lightland.npc.player.QuestHandler;
import com.hikarishima.lightland.npc.token.LocationVisitToken;
import com.hikarishima.lightland.npc.token.MobKillToken;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collection;

@SuppressWarnings("unused")
public class QuestEventHandler {

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
        QuestHandler parent = QuestHandler.get(event.player);
        Collection<LocationVisitToken> list = parent.getTokens(LocationVisitToken.class);
        for (LocationVisitToken token : list) {
            token.visit(event.player.getPosition(0));
        }
    }

}
