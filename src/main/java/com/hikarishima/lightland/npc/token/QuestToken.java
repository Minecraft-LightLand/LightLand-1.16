package com.hikarishima.lightland.npc.token;

import com.hikarishima.lightland.npc.player.PlayerProgress;
import com.lcy0x1.core.util.SerialClass;

@SerialClass
public abstract class QuestToken {

    @SerialClass.SerialField
    public String quest_id;

    public PlayerProgress progress;

}
