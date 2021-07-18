package com.hikarishima.lightland.registry;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.registry.item.MagicBook;
import com.hikarishima.lightland.registry.item.ArcaneAxe;
import com.hikarishima.lightland.registry.item.ArcaneSword;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;

import java.util.function.Function;

public class ItemRegistry extends ItemGroup {

    public static final ItemGroup ITEM_GROUP = new ItemRegistry();

    public static final MagicBook MAGIC_BOOK = regItem("magic_book", p -> new MagicBook(p.stacksTo(1)));
    public static final ArcaneSword ARCANE_SWORD = regItem("arcane_sword", p -> new ArcaneSword(ItemTier.GOLD, 5, 1, p));
    public static final ArcaneAxe ARCANE_AXE = regItem("arcane_axe", p -> new ArcaneAxe(ItemTier.GOLD, 9, 1.6f, p));

    public ItemRegistry() {
        super(LightLand.MODID);
    }

    public static <T extends Item> T regItem(String name, Function<Item.Properties, T> func) {
        T item = func.apply(new Item.Properties().tab(ITEM_GROUP));
        item.setRegistryName(LightLand.MODID, name);
        return item;
    }

    @Override
    public ItemStack makeIcon() {
        return MAGIC_BOOK.getDefaultInstance();
    }
}
