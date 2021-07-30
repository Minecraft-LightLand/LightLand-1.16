package com.hikarishima.lightland.npc.quest;

import com.hikarishima.lightland.npc.player.PlayerProgress;
import com.hikarishima.lightland.npc.player.QuestHandler;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

@SerialClass
public class QuestScene {

    public static QuestScene get(World world, String id) {
        return ConfigRecipe.getObject(world, ConfigRecipe.DIALOG, id);
    }

    public static PlayerProgress generate(PlayerEntity player, QuestHandler handler, String id) {
        PlayerProgress progress = new PlayerProgress();
        progress.quest_id = id;
        progress.stage_id = 0;
        progress.player = player;
        progress.handler = handler;
        progress.scene = get(player.level, id);
        return progress;
    }

    @SerialClass.SerialField
    public IQuestStage[] stage_list;

    @SerialClass.SerialField
    public String[] npc_lock; //TODO add npc lock

}
