package com.hikarishima.lightland.npc.token;

import com.hikarishima.lightland.npc.quest.MobKillStage;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.Entity;

@SerialClass
public class MobKillToken extends QuestToken {

    @SerialClass.SerialField
    public int count = 0;

    public void onKill(Entity entity) {
        MobKillStage stage = progress.getStage();
        if (stage.test(entity))
            count++;
        if (count >= stage.count)
            progress.proceed();
    }

}