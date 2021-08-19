package com.hikarishima.lightland.quest.trigger;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.quest.player.PlayerProgress;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.item.ItemStack;

@SerialClass
public class RewardTrigger extends QuestTrigger {

    @SerialClass.SerialField
    public ItemStack[] items;

    @SerialClass.SerialField
    public int vanilla_exp;

    @SerialClass.SerialField
    public int lightland_exp;

    @Override
    public void perform(PlayerProgress progress) {
        progress.player.giveExperiencePoints(vanilla_exp);
        for (ItemStack stack : items)
            progress.player.addItem(stack);
        MagicHandler.get(progress.player).abilityPoints.addExp(lightland_exp);
    }

}
