package com.hikarishima.lightland.quest.option;

import com.hikarishima.lightland.quest.player.QuestHandler;
import com.hikarishima.lightland.quest.token.DialogToken;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;

@SerialClass
public class DialogComplete implements IOptionComponent {

    @SerialClass.SerialField
    public String quest_id, stage_id;

    @Override
    public void perform(PlayerEntity player) {
        DialogToken token = QuestHandler.get(player).getToken(quest_id);
        if (token != null && token.stage_id.equals(stage_id))
            token.complete();
    }
}
