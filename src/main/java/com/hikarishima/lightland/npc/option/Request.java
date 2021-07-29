package com.hikarishima.lightland.npc.option;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@SerialClass
public class Request implements IOptionComponent {

    @SerialClass.SerialField
    public ItemStack[] item;

    @SerialClass.SerialField
    public int vanilla_level;

    public boolean test(PlayerEntity player) {
        if (player.experienceLevel < vanilla_level)
            return false;
        for (ItemStack required : item) {
            int count = 0;
            for (int i = 0; i < player.inventory.getContainerSize(); i++) {
                ItemStack stack = player.inventory.getItem(i);
                if (stack.isEmpty())
                    continue;
                if (ItemStack.isSame(required, stack) && ItemStack.tagMatches(required, stack)) {
                    count += stack.getCount();
                    if (count >= required.getCount())
                        break;
                }
            }
            if (count < required.getCount())
                return false;
        }
        return true;
    }

    public void perform(PlayerEntity player) {
        //TODO sidedness
        for (ItemStack required : item) {
            player.inventory.removeItem(required);
        }
        player.experienceLevel -= vanilla_level;
    }

}
