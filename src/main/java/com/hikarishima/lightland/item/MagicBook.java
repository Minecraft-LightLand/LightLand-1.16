package com.hikarishima.lightland.item;

import com.hikarishima.lightland.LightLand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class MagicBook extends Item {

    public MagicBook(Properties props) {
        super(props);
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide()) {
            player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1.0f);
            LightLand.proxy.openMagicBookGui();
        }
        return ActionResult.success(stack);
    }

}
