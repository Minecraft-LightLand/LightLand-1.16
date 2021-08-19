package com.hikarishima.lightland.npc.trigger;

import com.hikarishima.lightland.npc.player.PlayerProgress;
import com.lcy0x1.core.util.SerialClass;

@SerialClass
public abstract class QuestTrigger {

    public abstract void perform(PlayerProgress progress);

}
