package com.hikarishima.lightland.npc.quest;

import com.hikarishima.lightland.npc.player.PlayerProgress;
import com.hikarishima.lightland.npc.token.QuestToken;
import com.hikarishima.lightland.npc.trigger.QuestTrigger;
import com.lcy0x1.core.util.SerialClass;

public abstract class IQuestStage {

    @SerialClass.SerialField
    public QuestTrigger[] start_triggers, end_triggers;

    public void start(PlayerProgress progress) {
        if (start_triggers != null)
            for (QuestTrigger trigger : start_triggers) {
                trigger.perform(progress);
            }
        progress.handler.giveToken(progress.quest_id, genToken(progress));
    }

    public abstract QuestToken genToken(PlayerProgress progress);

    public void end(PlayerProgress progress) {
        if (end_triggers != null)
            for (QuestTrigger trigger : end_triggers) {
                trigger.perform(progress);
            }
        progress.handler.giveToken(progress.quest_id, null);
    }


}
