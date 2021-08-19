package com.hikarishima.lightland.registry.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FoiledItem extends Item {

    public FoiledItem(Properties props) {
        super(props);
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }

}
