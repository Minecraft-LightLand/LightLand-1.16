package com.hikarishima.lightland.npc.quest;

import com.hikarishima.lightland.npc.player.PlayerProgress;
import com.hikarishima.lightland.npc.token.MobKillToken;
import com.hikarishima.lightland.npc.token.QuestToken;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SerialClass
public class MobKillStage extends IQuestStage {

    @SerialClass.SerialField
    public ResourceLocation entity;

    @SerialClass.SerialField
    public int count;

    public boolean test(Entity e) {
        return entity.equals(e.getType().getRegistryName());
    }

    @Override
    public QuestToken genToken(PlayerProgress progress) {
        return new MobKillToken().init(progress);
    }
}
