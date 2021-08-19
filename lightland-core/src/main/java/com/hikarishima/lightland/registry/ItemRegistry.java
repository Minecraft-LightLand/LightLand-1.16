package com.hikarishima.lightland.registry;

import com.hikarishima.lightland.LightLand;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
public class ItemRegistry extends ItemGroup {

    public static final ItemGroup ITEM_GROUP = new ItemRegistry();

    public ItemRegistry() {
        super(LightLand.MODID);
    }

    private static <V extends T, T extends ForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(LightLand.MODID, name);
        return v;
    }

    public static <T extends Item> T regItem(String name, Function<Item.Properties, T> func) {
        T item = func.apply(new Item.Properties().tab(ITEM_GROUP));
        item.setRegistryName(LightLand.MODID, name);
        return item;
    }

    public static <T extends Item> T[] regArmor(String name, BiFunction<EquipmentSlotType, Item.Properties, T> func, T[] ans) {
        ans[0] = regItem(name + "helmet", (p) -> func.apply(EquipmentSlotType.HEAD, p));
        ans[1] = regItem(name + "chestplate", (p) -> func.apply(EquipmentSlotType.CHEST, p));
        ans[2] = regItem(name + "leggings", (p) -> func.apply(EquipmentSlotType.LEGS, p));
        ans[3] = regItem(name + "boots", (p) -> func.apply(EquipmentSlotType.FEET, p));
        return ans;
    }

    @Override
    public ItemStack makeIcon() {
        return Items.ENCHANTED_BOOK.getDefaultInstance();
    }

}
