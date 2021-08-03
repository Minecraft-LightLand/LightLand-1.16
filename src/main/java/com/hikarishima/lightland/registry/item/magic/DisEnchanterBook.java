package com.hikarishima.lightland.registry.item.magic;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.gui.block.DisEnchanterContainer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DisEnchanterBook extends Item implements INamedContainerProvider {

    public static final ITextComponent TITLE = Translator.getContainer("disenchant");

    public DisEnchanterBook(Properties props) {
        super(props);
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide()) {
            player.openMenu(this);
        } else {
            player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1.0f);
        }
        return ActionResult.success(stack);
    }

    @Override
    public ITextComponent getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public Container createMenu(int wid, PlayerInventory plInv, PlayerEntity pl) {
        return new DisEnchanterContainer(wid, plInv);
    }
}
