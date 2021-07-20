package com.hikarishima.lightland.registry;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.registry.item.FoiledItem;
import com.hikarishima.lightland.registry.item.combat.*;
import com.hikarishima.lightland.registry.item.magic.ArcaneAxe;
import com.hikarishima.lightland.registry.item.magic.ArcaneSword;
import com.hikarishima.lightland.registry.item.magic.MagicBook;
import com.hikarishima.lightland.registry.item.magic.MagicScroll;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;

import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
public class ItemRegistry extends ItemGroup {

    public static final ItemGroup ITEM_GROUP = new ItemRegistry();

    public static final MagicBook MAGIC_BOOK = regItem("magic_book", p -> new MagicBook(p.stacksTo(1)));
    public static final ArcaneSword ARCANE_SWORD_GILDED = regItem("gilded_arcane_sword", p -> new ArcaneSword(ItemTier.IRON, 5, -2.4f, p.stacksTo(1).setNoRepair(), 10));
    public static final ArcaneAxe ARCANE_AXE_GILDED = regItem("gilded_arcane_axe", p -> new ArcaneAxe(ItemTier.IRON, 8, -3.1f, p.stacksTo(1).setNoRepair(), 10));
    public static final Item ENCHANT_GOLD_NUGGET = regItem("enchant_gold_nugget", FoiledItem::new);
    public static final Item ENCHANT_GOLD_INGOT = regItem("enchant_gold_ingot", FoiledItem::new);
    public static final Item ENCHANT_ALLOY_INGOT = regItem("enchant_alloy_ingot", FoiledItem::new);
    public static final Item ENCHANT_LIGHT_INGOT = regItem("enchant_light_ingot", FoiledItem::new);
    public static final Item ANTI_MAGIC_METAL = regItem("anti_magic_metal_ingot", Item::new);
    public static final Item LIGHT_ALLOY = regItem("light_alloy_ingot", Item::new);
    public static final Item ANTI_MAGIC_ALLOY = regItem("anti_magic_alloy_ingot", Item::new);
    public static final AntiMagicArmor[] AMM_ARMOR = regArmor("anti_magic_metal_", (s, p) -> new AntiMagicArmor(AntiMagicArmorMaterial.METAL, s, p), new AntiMagicArmor[4]);
    public static final AntiMagicArmor[] LA_ARMOR = regArmor("light_alloy_", (s, p) -> new AntiMagicArmor(AntiMagicArmorMaterial.LIGHT, s, p), new AntiMagicArmor[4]);
    public static final AntiMagicArmor[] AMA_ARMOR = regArmor("anti_magic_alloy_", (s, p) -> new AntiMagicArmor(AntiMagicArmorMaterial.ALLOY, s, p), new AntiMagicArmor[4]);
    public static final AntiMagicSword AMM_SWORD = regItem("anti_magic_metal_sword", (p) -> new AntiMagicSword(AntiMagicItemTier.METAL, 5, -2.4f, p.stacksTo(1)));
    public static final AntiMagicSword LA_SWORD = regItem("light_alloy_sword", (p) -> new AntiMagicSword(AntiMagicItemTier.LIGHT, 5, -2f, p.stacksTo(1)));
    public static final AntiMagicSword AMA_SWORD = regItem("anti_magic_alloy_sword", (p) -> new AntiMagicSword(AntiMagicItemTier.ALLOY, 7, -2.4f, p.stacksTo(1)));
    public static final AntiMagicAxe AMM_AXE = regItem("anti_magic_metal_axe", (p) -> new AntiMagicAxe(AntiMagicItemTier.METAL, 8, -3.1f, p.stacksTo(1)));
    public static final AntiMagicAxe LA_AXE = regItem("light_alloy_axe", (p) -> new AntiMagicAxe(AntiMagicItemTier.LIGHT, 8, -2.4f, p.stacksTo(1)));
    public static final AntiMagicAxe AMA_AXE = regItem("anti_magic_alloy_axe", (p) -> new AntiMagicAxe(AntiMagicItemTier.ALLOY, 10, -3f, p.stacksTo(1)));
    public static final MagicScroll SPELL_CARD = regItem("spell_card", (p) -> new MagicScroll(MagicScroll.ScrollType.CARD, p));
    public static final MagicScroll SPELL_PARCHMENT = regItem("spell_parchment", (p) -> new MagicScroll(MagicScroll.ScrollType.PARCHMENT, p));
    public static final MagicScroll SPELL_SCROLL = regItem("spell_scroll", (p) -> new MagicScroll(MagicScroll.ScrollType.SCROLL, p));

    public ItemRegistry() {
        super(LightLand.MODID);
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
        return MAGIC_BOOK.getDefaultInstance();
    }

}
