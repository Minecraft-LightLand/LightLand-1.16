package com.hikarishima.lightland.magic.registry.item.combat;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArmorBag extends Item {

    public ArmorBag(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide())
            return ActionResult.success(stack);
        NonNullList<ItemStack> list = NonNullList.withSize(64, ItemStack.EMPTY);
        CompoundNBT tag = stack.getOrCreateTagElement("BlockEntityTag");
        if (tag.contains("Items")) {
            ItemStackHelper.loadAllItems(stack.getOrCreateTag(), list);
        }
        if (player.isShiftKeyDown()) {
            throwOut(list, player, world);
        } else {
            Queue<Holder<ItemStack>> queue = new ArrayDeque<>();
            player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    .resolve().ifPresent(e -> {
                for (int i = 9; i < 36; i++) {
                    ItemStack inv_stack = player.inventory.items.get(i);
                    if (inv_stack.isDamageableItem()) {
                        int finalI = i;
                        queue.add(new Holder<>(
                                () -> e.getStackInSlot(finalI),
                                () -> e.extractItem(finalI, 1, false)));
                    }
                }
            });
            add(list, queue);
        }
        ItemStackHelper.saveAllItems(tag, list);
        return ActionResult.success(stack);
    }

    private void throwOut(NonNullList<ItemStack> list, PlayerEntity player, World world) {
        for (ItemStack stack : list) {
            if (!stack.isEmpty()) {
                player.inventory.placeItemBackInInventory(world, stack);
            }
        }
        list.clear();
    }

    private static void add(NonNullList<ItemStack> list, Queue<Holder<ItemStack>> toAdd) {
        for (int i = 0; i < 64; i++) {
            if (list.get(i).isEmpty()) {
                if (toAdd.isEmpty()) return;
                Holder<ItemStack> item = toAdd.poll();
                list.set(i, item.getter.get());
                item.remove.run();
            }
        }
    }

    private static class Holder<T> {

        final Supplier<T> getter;
        final Runnable remove;

        private Holder(Supplier<T> getter, Runnable remove) {
            this.getter = getter;
            this.remove = remove;
        }
    }


}
