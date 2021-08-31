package com.hikarishima.lightland.registry;

import com.hikarishima.lightland.LightLand;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
public class ItemRegistry extends ItemGroup {

    public static final ItemGroup ITEM_GROUP = new ItemRegistry();

    public ItemRegistry() {
        super(LightLand.MODID);
    }

    @Override
    public ItemStack makeIcon() {
        return Items.ENCHANTED_BOOK.getDefaultInstance();
    }

}
