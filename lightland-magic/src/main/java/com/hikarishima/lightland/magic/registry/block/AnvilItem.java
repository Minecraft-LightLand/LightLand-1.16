package com.hikarishima.lightland.magic.registry.block;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AnvilItem extends BlockItem {

    public AnvilItem(Block block, Item.Properties props) {
        super(block, props);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return stack.getCount() == 1 && (enchantment.category.canEnchant(this));
    }


    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    public int getItemEnchantability(ItemStack stack) {
        return 80;
    }

}
