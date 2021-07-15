package com.hikarishima.lightland.item;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.gui.MagicBookContainer;
import com.lcy0x1.core.util.NBTObj;
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
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class MagicBook extends Item implements INamedContainerProvider {

    public static final ITextComponent TITLE = Translator.getContainer("magic_book.main");

    public MagicBook(Properties props) {
        super(props);
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide()) {
            player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1.0f);
        } else {
            player.openMenu(this);
        }
        return ActionResult.success(stack);
    }

    @Override
    public ITextComponent getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
        return new MagicBookContainer(id, inv);
    }

}
