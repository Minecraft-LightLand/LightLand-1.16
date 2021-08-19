package com.hikarishima.lightland.quest.option;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@SerialClass
public class Request implements IOptionComponent {

    @SerialClass.SerialField
    public ItemStack[] items;

    @SerialClass.SerialField
    public int vanilla_level;

    public boolean test(PlayerEntity player) {
        if (player.experienceLevel < vanilla_level)
            return false;
        for (ItemStack required : items) {
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
        for (ItemStack required : items) {
            int count = required.getCount();
            for (int i = 0; i < player.inventory.getContainerSize(); i++) {
                ItemStack stack = player.inventory.getItem(i);
                if (stack.isEmpty())
                    continue;
                if (ItemStack.isSame(required, stack) && ItemStack.tagMatches(required, stack)) {
                    if (count >= stack.getCount()) {
                        player.inventory.setItem(i, ItemStack.EMPTY);
                        count -= stack.getCount();
                        if (count == 0)
                            break;
                    } else {
                        stack.shrink(count);
                        break;
                    }
                }
            }
        }
        player.experienceLevel -= vanilla_level;
    }

}
