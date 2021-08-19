package com.hikarishima.lightland.quest.option;

import com.hikarishima.lightland.quest.player.QuestHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;

@SerialClass
public class QuestStart implements IOptionComponent {

    @SerialClass.SerialField
    public String quest_id;

    public void perform(PlayerEntity player) {
        QuestHandler.get(player).startQuest(quest_id);
    }

}
