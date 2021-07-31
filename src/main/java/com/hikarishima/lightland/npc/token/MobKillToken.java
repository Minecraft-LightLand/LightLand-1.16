package com.hikarishima.lightland.npc.token;

import com.hikarishima.lightland.npc.player.QuestToClient;
import com.hikarishima.lightland.npc.quest.MobKillStage;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;

@SerialClass
public class MobKillToken extends QuestToken {

    @SerialClass.SerialField
    public int count = 0;

    public void onKill(Entity entity) {
        MobKillStage stage = progress.getStage();
        if (stage.test(entity)) {
            onKill();
        }
    }

    public void onKill() {
        if (!progress.player.level.isClientSide()) {
            PacketHandler.toClient((ServerPlayerEntity) progress.player, QuestToClient.onKill(quest_id));
        }
        count++;
        MobKillStage stage = progress.getStage();
        if (count >= stage.count)
            progress.proceed();
    }

}
