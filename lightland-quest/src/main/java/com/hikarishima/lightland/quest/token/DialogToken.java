package com.hikarishima.lightland.quest.token;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

@SerialClass
public class DialogToken extends QuestToken {

    @SerialClass.SerialField
    public String stage_id;

    public void complete() {
        progress.proceed();
    }

    @Override
    public ITextComponent getQuestProgressText() {
        return new StringTextComponent(getDescription());
    }
}
