package com.hikarishima.lightland.quest.token;

import com.hikarishima.lightland.quest.player.PlayerProgress;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

@SerialClass
public abstract class QuestToken {

    @SerialClass.SerialField
    public String quest_id;

    public PlayerProgress progress;

    public QuestToken init(PlayerProgress prog) {
        progress = prog;
        quest_id = prog.quest_id;
        return this;
    }

    public ITextComponent getTitle() {
        return new StringTextComponent(progress.getStage().title);
    }

    public String getDescription() {
        return progress.getStage().description;
    }

    public abstract ITextComponent getQuestProgressText();

}
