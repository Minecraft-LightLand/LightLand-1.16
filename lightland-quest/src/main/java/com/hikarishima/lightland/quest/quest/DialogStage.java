package com.hikarishima.lightland.quest.quest;

import com.hikarishima.lightland.quest.player.PlayerProgress;
import com.hikarishima.lightland.quest.token.DialogToken;
import com.hikarishima.lightland.quest.token.QuestToken;
import com.lcy0x1.core.util.SerialClass;

@SerialClass
public class DialogStage extends IQuestStage {

    @SerialClass.SerialField
    public String stage_id;

    @Override
    public QuestToken genToken(PlayerProgress progress) {
        DialogToken token = new DialogToken();
        token.stage_id = stage_id;
        return token.init(progress);
    }

}
