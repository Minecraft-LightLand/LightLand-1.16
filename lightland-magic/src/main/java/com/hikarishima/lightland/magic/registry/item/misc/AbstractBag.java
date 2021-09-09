package com.hikarishima.lightland.magic.registry.item.misc;

import com.hikarishima.lightland.magic.Translator;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractBag extends Item {

    public AbstractBag(Properties props) {
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
            ItemStackHelper.loadAllItems(tag, list);
        }
        if (player.isShiftKeyDown()) {
            throwOut(list, player, world);
        } else {
            Queue<Holder<ItemStack>> queue = new ArrayDeque<>();
            player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    .resolve().ifPresent(e -> {
                for (int i = 9; i < 36; i++) {
                    ItemStack inv_stack = player.inventory.items.get(i);
                    if (matches(stack, inv_stack)) {
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

    public abstract boolean matches(ItemStack self, ItemStack stack);

    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        list.add(Translator.get("tooltip.armor_bag.size", getSize(stack), 64));
        list.add(Translator.get("tooltip.armor_bag.info"));
    }

    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    public double getDurabilityForDisplay(ItemStack stack) {
        return 1 - getSize(stack) / 64f;
    }

    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return 0xFFFFFF;
    }

    private int getSize(ItemStack stack) {
        NonNullList<ItemStack> list = NonNullList.withSize(64, ItemStack.EMPTY);
        CompoundNBT tag = stack.getOrCreateTagElement("BlockEntityTag");
        if (tag.contains("Items")) {
            ItemStackHelper.loadAllItems(tag, list);
        }
        int ans = 0;
        for (ItemStack is : list) {
            if (!is.isEmpty()) {
                ans++;
            }
        }
        return ans;
    }

    private void throwOut(NonNullList<ItemStack> list, PlayerEntity player, World world) {
        for (ItemStack stack : list) {
            if (!stack.isEmpty()) {
                player.inventory.placeItemBackInInventory(world, stack.copy());
            }
        }
        list.clear();
    }

    private static void add(NonNullList<ItemStack> list, Queue<Holder<ItemStack>> toAdd) {
        for (int i = 0; i < 64; i++) {
            if (list.get(i).isEmpty()) {
                if (toAdd.isEmpty()) return;
                Holder<ItemStack> item = toAdd.poll();
                list.set(i, item.getter.get().copy());
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
