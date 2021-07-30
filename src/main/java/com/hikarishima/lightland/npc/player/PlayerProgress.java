package com.hikarishima.lightland.npc.player;

import com.hikarishima.lightland.npc.quest.IQuestStage;
import com.hikarishima.lightland.npc.quest.QuestScene;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;

@SerialClass
public class PlayerProgress {

    @SerialClass.SerialField
    public String quest_id;

    @SerialClass.SerialField
    public int stage_id;

    public PlayerEntity player;
    public QuestHandler handler;
    public QuestScene scene;

    public void proceed() {
        if (isCompleted())
            return;
        scene.stage_list[stage_id].end(this);
        stage_id++;
        if (scene.stage_list.length > stage_id)
            scene.stage_list[stage_id].start(this);
        else {
            //TODO complete quest
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends IQuestStage> T getStage() {
        if (stage_id >= scene.stage_list.length)
            return null;
        return (T) scene.stage_list[stage_id];
    }

    public void init() {
        if (scene.stage_list.length > stage_id)
            scene.stage_list[stage_id].start(this);
    }

    public boolean isCompleted() {
        return scene.stage_list.length <= stage_id;
    }

}
