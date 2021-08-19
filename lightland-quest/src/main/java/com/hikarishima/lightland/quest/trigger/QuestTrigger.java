package com.hikarishima.lightland.quest.trigger;

import com.hikarishima.lightland.quest.player.PlayerProgress;
import com.lcy0x1.core.util.SerialClass;

@SerialClass
public abstract class QuestTrigger {

    public abstract void perform(PlayerProgress progress);

}
