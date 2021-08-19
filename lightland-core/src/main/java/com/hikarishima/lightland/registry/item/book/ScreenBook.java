package com.hikarishima.lightland.registry.item.book;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ScreenBook extends Item {

    public Supplier<Supplier<?>> sup;

    public ScreenBook(Properties props, Supplier<Supplier<?>> sup) {
        super(props.stacksTo(1));
        this.sup = sup;
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide()) {
            player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1.0f);
            Minecraft.getInstance().setScreen((Screen) sup.get().get());
        }
        return ActionResult.success(stack);
    }

}
