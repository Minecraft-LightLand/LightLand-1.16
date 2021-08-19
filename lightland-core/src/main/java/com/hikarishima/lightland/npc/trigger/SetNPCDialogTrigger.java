package com.hikarishima.lightland.npc.trigger;

import com.hikarishima.lightland.npc.player.PlayerProgress;
import com.lcy0x1.core.util.SerialClass;

@SerialClass
public class SetNPCDialogTrigger extends QuestTrigger {

    @SerialClass.SerialField
    public String npc;

    @SerialClass.SerialField
    public String selector;

    @Override
    public void perform(PlayerProgress progress) {
        progress.handler.setDialog(progress, npc, selector);
    }
}
