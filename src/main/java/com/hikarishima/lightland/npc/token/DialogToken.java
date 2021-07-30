package com.hikarishima.lightland.npc.token;

import com.lcy0x1.core.util.SerialClass;

@SerialClass
public class DialogToken extends QuestToken {

    @SerialClass.SerialField
    public String stage_id;

    public void complete() {
        progress.proceed();
    }

}
