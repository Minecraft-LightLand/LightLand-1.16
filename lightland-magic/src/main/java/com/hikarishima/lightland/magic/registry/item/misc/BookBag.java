package com.hikarishima.lightland.magic.registry.item.misc;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BookBag extends AbstractBag {

    public BookBag(Properties props) {
        super(props);
    }

    @Override
    public boolean matches(ItemStack self, ItemStack stack) {
        return stack.getItem() == Items.ENCHANTED_BOOK;
    }


}
