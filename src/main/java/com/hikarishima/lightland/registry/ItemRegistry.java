package com.hikarishima.lightland.registry;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.gui.ability.ProfessionScreen;
import com.hikarishima.lightland.magic.gui.magic_tree.MagicTreeScreen;
import com.hikarishima.lightland.npc.gui.QuestScreen;
import com.hikarishima.lightland.registry.block.TempBlock;
import com.hikarishima.lightland.registry.item.FoiledItem;
import com.hikarishima.lightland.registry.item.book.ContainerBook;
import com.hikarishima.lightland.registry.item.book.ScreenBook;
import com.hikarishima.lightland.registry.item.combat.*;
import com.hikarishima.lightland.registry.item.magic.*;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
public class ItemRegistry extends ItemGroup {

    public static final ItemGroup ITEM_GROUP = new ItemRegistry();
    private static final int MANA = 256;

    public static final ScreenBook MAGIC_BOOK = regItem("magic_book", p -> new ScreenBook(p, () -> MagicTreeScreen::new));
    public static final ScreenBook QUEST_BOOK = regItem("quest_book", p -> new ScreenBook(p, () -> QuestScreen::new));
    public static final ScreenBook ABILITY_BOOK = regItem("ability_book", p -> new ScreenBook(p, () -> ProfessionScreen::new));
    public static final ContainerBook DISENCHANT_BOOK = regItem("disenchant_book", p -> new ContainerBook(p, ContainerRegistry.CT_DISENCH));
    public static final ContainerBook MAGIC_CRAFT_BOOK = regItem("magic_craft_book", p -> new ContainerBook(p, ContainerRegistry.CT_MAGIC_CRAFT));
    public static final ContainerBook SPELL_CRAFT_BOOK = regItem("spell_craft_book", p -> new ContainerBook(p, ContainerRegistry.CT_SPELL_CRAFT));
    public static final ContainerBook ARCANE_INJECT_BOOK = regItem("arcane_inject_book", p -> new ContainerBook(p, ContainerRegistry.CT_ARCANE_INJECT));
    public static final ArcaneSword ARCANE_SWORD_GILDED = regItem("gilded_arcane_sword", p -> new ArcaneSword(ItemTier.IRON, 5, -2.4f, p.stacksTo(1).setNoRepair(), 10));
    public static final ArcaneAxe ARCANE_AXE_GILDED = regItem("gilded_arcane_axe", p -> new ArcaneAxe(ItemTier.IRON, 8, -3.1f, p.stacksTo(1).setNoRepair(), 10));
    public static final Item ENCHANT_GOLD_NUGGET = regItem("enchant_gold_nugget", p -> new ManaStorage(p, Items.GOLD_NUGGET, MANA));
    public static final Item ENCHANT_GOLD_INGOT = regItem("enchant_gold_ingot", p -> new ManaStorage(p, Items.GOLD_INGOT, MANA * 9));
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
    public static final MagicWand GILDED_WAND = regItem("gilded_wand", MagicWand::new);
    public static final RecordPearl RECORD_PEARL = regItem("record_pearl", RecordPearl::new);
    public static final Item ENCHANT_COOKIE = regItem("enchant_cookie", p -> new ManaStorage(p.food(Foods.COOKIE), Items.COOKIE, MANA / 8));
    public static final Item ENCHANT_MELON = regItem("enchant_melon", p -> new ManaStorage(p.food(Foods.MELON_SLICE), Items.MELON, MANA));
    public static final Item ENCHANT_CARROT = regItem("enchant_carrot", p -> new ManaStorage(p.food(Foods.GOLDEN_CARROT), Items.GOLDEN_CARROT, MANA * 8));
    public static final Item ENCHANT_APPLE = regItem("enchant_apple", p -> new ManaStorage(p.food(Foods.ENCHANTED_GOLDEN_APPLE), Items.GOLDEN_APPLE, MANA * 72));

    public static final TempBlock TEMP_DIRT = reg("temp_dirt", new TempBlock(AbstractBlock.Properties.copy(Blocks.DIRT).noDrops()));
    public static final TempBlock TEMP_COBBLE = reg("temp_cobblestone", new TempBlock(AbstractBlock.Properties.copy(Blocks.COBBLESTONE).noDrops()));

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
        return MAGIC_BOOK.getDefaultInstance();
    }

}
