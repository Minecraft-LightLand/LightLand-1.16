package com.hikarishima.lightland.quest.trigger;

import com.hikarishima.lightland.quest.player.PlayerProgress;
import com.lcy0x1.core.util.SerialClass;

@SerialClass
public class QuestUnlockTrigger extends QuestTrigger {

    @SerialClass.SerialField
    public String quest_id;

    @Override
    public void perform(PlayerProgress progress) {
        progress.handler.startQuest(quest_id);
    }

}
