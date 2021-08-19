package com.hikarishima.lightland.quest.option;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@SerialClass
public class Reward implements IOptionComponent {

    @SerialClass.SerialField
    public ItemStack[] items;

    @SerialClass.SerialField
    public int vanilla_exp;

    @SerialClass.SerialField
    public int lightland_exp;

    public void perform(PlayerEntity player) {
        player.giveExperiencePoints(vanilla_exp);
        for (ItemStack stack : items)
            player.addItem(stack);
        MagicHandler.get(player).abilityPoints.addExp(lightland_exp);
    }

}
