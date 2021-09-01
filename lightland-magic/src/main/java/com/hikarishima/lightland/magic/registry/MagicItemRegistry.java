package com.hikarishima.lightland.magic.registry;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.gui.ability.ProfessionScreen;
import com.hikarishima.lightland.magic.gui.container.ArcaneInjectContainer;
import com.hikarishima.lightland.magic.gui.container.ChemContainer;
import com.hikarishima.lightland.magic.gui.container.DisEnchanterContainer;
import com.hikarishima.lightland.magic.gui.container.SpellCraftContainer;
import com.hikarishima.lightland.magic.gui.magic_tree.MagicTreeScreen;
import com.hikarishima.lightland.magic.registry.block.RitualCore;
import com.hikarishima.lightland.magic.registry.block.RitualSide;
import com.hikarishima.lightland.magic.registry.block.TempBlock;
import com.hikarishima.lightland.magic.registry.item.combat.*;
import com.hikarishima.lightland.magic.registry.item.magic.*;
import com.hikarishima.lightland.registry.ItemRegistry;
import com.hikarishima.lightland.registry.item.ContainerBook;
import com.hikarishima.lightland.registry.item.FoiledItem;
import com.hikarishima.lightland.registry.item.ScreenBook;
import com.lcy0x1.base.block.BlockProxy;
import com.lcy0x1.base.block.LightLandBlock;
import com.lcy0x1.base.block.LightLandBlockProperties;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class MagicItemRegistry {

    public static final DeferredRegister<Item> ITEM = DeferredRegister.create(Item.class, LightLandMagic.MODID);
    public static final DeferredRegister<Block> BLOCK = DeferredRegister.create(Block.class, LightLandMagic.MODID);

    private static final int MANA = 256;
    private static final LightLandBlockProperties PEDESTAL = LightLandBlockProperties.copy(Blocks.STONE).make(e -> e.noOcclusion().lightLevel(bs -> bs.getValue(BlockStateProperties.LIT) ? 15 : 7));

    public static final RegistryObject<Block> B_ENCHANT_GOLD = reg("enchant_gold_block", () -> new Block(AbstractBlock.Properties.copy(Blocks.GOLD_BLOCK)));
    public static final RegistryObject<Block> B_ENCHANT_ALLOY = reg("enchant_alloy_block", () -> new Block(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> B_PERMANENCE_IRON = reg("permanence_iron_block", () -> new Block(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> B_ENCHANT_LIGHT = reg("enchant_light_block", () -> new Block(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> B_ANTI_MAGIC_METAL = reg("anti_magic_metal_block", () -> new Block(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> B_LIGHT_ALLOY = reg("light_alloy_block", () -> new Block(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> B_ANTI_MAGIC_ALLOY = reg("anti_magic_alloy_block", () -> new Block(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<TempBlock> TEMP_DIRT = reg("temp_dirt", () -> new TempBlock(AbstractBlock.Properties.copy(Blocks.DIRT).noDrops()));
    public static final RegistryObject<TempBlock> TEMP_COBBLE = reg("temp_cobblestone", () -> new TempBlock(AbstractBlock.Properties.copy(Blocks.COBBLESTONE).noDrops()));
    public static final RegistryObject<LightLandBlock> B_RITUAL_CORE = reg("ritual_core", () -> LightLandBlock.newBaseBlock(PEDESTAL, RitualCore.ACTIVATE, RitualCore.CLICK, BlockProxy.TRIGGER, RitualCore.TILE_ENTITY_SUPPLIER_BUILDER));
    public static final RegistryObject<LightLandBlock> B_RITUAL_SIDE = reg("ritual_side", () -> LightLandBlock.newBaseBlock(PEDESTAL, RitualCore.CLICK, RitualSide.TILE_ENTITY_SUPPLIER_BUILDER));
    public static final RegistryObject<Block> B_ANVIL = reg("anvil", () -> new AnvilBlock(AbstractBlock.Properties.copy(Blocks.ANVIL)));

    public static final RegistryObject<ScreenBook> MAGIC_BOOK = regItem("magic_book", p -> new ScreenBook(p, () -> MagicTreeScreen::new));
    public static final RegistryObject<ScreenBook> ABILITY_BOOK = regItem("ability_book", p -> new ScreenBook(p, () -> ProfessionScreen::new));
    public static final RegistryObject<ContainerBook> DISENCHANT_BOOK = regItem("disenchant_book", p -> new ContainerBook(p, MagicContainerRegistry.CT_DISENCH::get, (a, b, c) -> new DisEnchanterContainer(a, b)));
    public static final RegistryObject<ContainerBook> SPELL_CRAFT_BOOK = regItem("spell_craft_book", p -> new ContainerBook(p, MagicContainerRegistry.CT_SPELL_CRAFT::get, (a, b, c) -> new SpellCraftContainer(a, b)));
    public static final RegistryObject<ContainerBook> ARCANE_INJECT_BOOK = regItem("arcane_inject_book", p -> new ContainerBook(p, MagicContainerRegistry.CT_ARCANE_INJECT::get, (a, b, c) -> new ArcaneInjectContainer(a, b)));
    public static final RegistryObject<ContainerBook> CHEM_BOOK = regItem("chemistry_book", p -> new ContainerBook(p, MagicContainerRegistry.CT_CHEM::get, (a, b, c) -> new ChemContainer(a, b)));
    public static final RegistryObject<ArcaneSword> ARCANE_SWORD_GILDED = regItem("gilded_arcane_sword", p -> new ArcaneSword(ItemTier.IRON, 5, -2.4f, p.stacksTo(1).setNoRepair(), 50));
    public static final RegistryObject<ArcaneAxe> ARCANE_AXE_GILDED = regItem("gilded_arcane_axe", p -> new ArcaneAxe(ItemTier.IRON, 8, -3.1f, p.stacksTo(1).setNoRepair(), 50));
    public static final RegistryObject<Item> ENCHANT_GOLD_NUGGET = regItem("enchant_gold_nugget", p -> new ManaStorage(p, Items.GOLD_NUGGET, MANA));
    public static final RegistryObject<Item> ENCHANT_GOLD_INGOT = regItem("enchant_gold_ingot", p -> new ManaStorage(p, Items.GOLD_INGOT, MANA * 9));
    public static final RegistryObject<Item> ENCHANT_ALLOY_INGOT = regItem("enchant_alloy_ingot", FoiledItem::new);
    public static final RegistryObject<Item> PERMANENCE_IRON_INGOT = regItem("permanence_iron_ingot", FoiledItem::new);
    public static final RegistryObject<Item> ENCHANT_LIGHT_INGOT = regItem("enchant_light_ingot", FoiledItem::new);
    public static final RegistryObject<Item> ANTI_MAGIC_METAL = regItem("anti_magic_metal_ingot", Item::new);
    public static final RegistryObject<Item> LIGHT_ALLOY = regItem("light_alloy_ingot", Item::new);
    public static final RegistryObject<Item> ANTI_MAGIC_ALLOY = regItem("anti_magic_alloy_ingot", Item::new);
    public static final RegistryObject<MagicArmor>[] AMM_ARMOR = regArmor("anti_magic_metal_", (s, p) -> new MagicArmor(MagicArmorMaterial.METAL, s, p));
    public static final RegistryObject<MagicArmor>[] LA_ARMOR = regArmor("light_alloy_", (s, p) -> new MagicArmor(MagicArmorMaterial.LIGHT, s, p));
    public static final RegistryObject<MagicArmor>[] AMA_ARMOR = regArmor("anti_magic_alloy_", (s, p) -> new MagicArmor(MagicArmorMaterial.ALLOY, s, p));
    public static final RegistryObject<MagicArmor>[] PER_ARMOR = regArmor("permanence_iron_", (s, p) -> new MagicArmor(MagicArmorMaterial.PERMANENCE, s, p));
    public static final RegistryObject<MagicSword> AMM_SWORD = regItem("anti_magic_metal_sword", (p) -> new MagicSword(MagicItemTier.METAL, 5, -2.4f, p.stacksTo(1)));
    public static final RegistryObject<MagicSword> LA_SWORD = regItem("light_alloy_sword", (p) -> new MagicSword(MagicItemTier.LIGHT, 5, -2f, p.stacksTo(1)));
    public static final RegistryObject<MagicSword> AMA_SWORD = regItem("anti_magic_alloy_sword", (p) -> new MagicSword(MagicItemTier.ALLOY, 7, -2.4f, p.stacksTo(1)));
    public static final RegistryObject<MagicSword> PER_SWORD = regItem("permanence_iron_sword", (p) -> new MagicSword(MagicItemTier.PERMANENCE, 5, -2.4f, p.stacksTo(1)));
    public static final RegistryObject<MagicAxe> AMM_AXE = regItem("anti_magic_metal_axe", (p) -> new MagicAxe(MagicItemTier.METAL, 8, -3.1f, p.stacksTo(1)));
    public static final RegistryObject<MagicAxe> LA_AXE = regItem("light_alloy_axe", (p) -> new MagicAxe(MagicItemTier.LIGHT, 8, -2.4f, p.stacksTo(1)));
    public static final RegistryObject<MagicAxe> AMA_AXE = regItem("anti_magic_alloy_axe", (p) -> new MagicAxe(MagicItemTier.ALLOY, 10, -3f, p.stacksTo(1)));
    public static final RegistryObject<MagicAxe> PER_AXE = regItem("permanence_iron_axe", (p) -> new MagicAxe(MagicItemTier.PERMANENCE, 8, -3.1f, p.stacksTo(1)));
    public static final RegistryObject<MagicScroll> SPELL_CARD = regItem("spell_card", (p) -> new MagicScroll(MagicScroll.ScrollType.CARD, p));
    public static final RegistryObject<MagicScroll> SPELL_PARCHMENT = regItem("spell_parchment", (p) -> new MagicScroll(MagicScroll.ScrollType.PARCHMENT, p));
    public static final RegistryObject<MagicScroll> SPELL_SCROLL = regItem("spell_scroll", (p) -> new MagicScroll(MagicScroll.ScrollType.SCROLL, p));
    public static final RegistryObject<MagicWand> GILDED_WAND = regItem("gilded_wand", MagicWand::new);
    public static final RegistryObject<RecordPearl> RECORD_PEARL = regItem("record_pearl", RecordPearl::new);
    public static final RegistryObject<Item> ENCHANT_COOKIE = regItem("enchant_cookie", p -> new ManaStorage(p.food(Foods.COOKIE), Items.COOKIE, MANA / 8));
    public static final RegistryObject<Item> ENCHANT_MELON = regItem("enchant_melon", p -> new ManaStorage(p.food(Foods.MELON_SLICE), Items.MELON, MANA));
    public static final RegistryObject<Item> ENCHANT_CARROT = regItem("enchant_carrot", p -> new ManaStorage(p.food(Foods.GOLDEN_CARROT), Items.GOLDEN_CARROT, MANA * 8));
    public static final RegistryObject<Item> ENCHANT_APPLE = regItem("enchant_apple", p -> new ManaStorage(p.food(Foods.ENCHANTED_GOLDEN_APPLE), Items.GOLDEN_APPLE, MANA * 72));
    public static final RegistryObject<Item> ENCHANT_STRING = regItem("enchant_string", FoiledItem::new);
    public static final RegistryObject<Item> ENCHANT_CLOTH = regItem("enchant_cloth", FoiledItem::new);
    public static final RegistryObject<Item> ENCHANT_CHAIN = regItem("enchant_chain", FoiledItem::new);
    public static final RegistryObject<MagicArmor>[] CLOTH_ARMOR = regArmor("enchant_cloth_", (s, p) -> new MagicArmor(MagicArmorMaterial.CLOTH, s, p));
    public static final RegistryObject<MagicArmor>[] CLOTH_CHAIN = regArmor("enchant_chain_", (s, p) -> new MagicArmor(MagicArmorMaterial.CHAIN, s, p));
    public static final RegistryObject<MagicArmor>[] CLOTH_LIGHT = regArmor("enchant_light_", (s, p) -> new MagicArmor(MagicArmorMaterial.ENCH_LIGHT, s, p));
    public static final RegistryObject<ArmorBag> ARMOR_BAG = regItem("armor_bag", ArmorBag::new);

    public static final RegistryObject<BlockItem> I_ENCHANT_GOLD = regBlockItem(B_ENCHANT_GOLD);
    public static final RegistryObject<BlockItem> I_ENCHANT_ALLOY = regBlockItem(B_ENCHANT_ALLOY);
    public static final RegistryObject<BlockItem> I_PERMANENCE_IRON = regBlockItem(B_PERMANENCE_IRON);
    public static final RegistryObject<BlockItem> I_ENCHANT_LIGHT = regBlockItem(B_ENCHANT_LIGHT);
    public static final RegistryObject<BlockItem> I_ANTI_MAGIC_METAL = regBlockItem(B_ANTI_MAGIC_METAL);
    public static final RegistryObject<BlockItem> I_LIGHT_ALLOY = regBlockItem(B_LIGHT_ALLOY);
    public static final RegistryObject<BlockItem> I_ANTI_MAGIC_ALLOY = regBlockItem(B_ANTI_MAGIC_ALLOY);
    public static final RegistryObject<BlockItem> I_RITUAL_CORE = regBlockItem(B_RITUAL_CORE);
    public static final RegistryObject<BlockItem> I_RITUAL_SIDE = regBlockItem(B_RITUAL_SIDE);
    public static final RegistryObject<BlockItem> I_ANVIL = regBlockItem(B_ANVIL);

    private static <V extends Block> RegistryObject<V> reg(String name, Supplier<V> v) {
        return BLOCK.register(name, v);
    }

    private static <T extends Item> RegistryObject<T> regItem(String name, Function<Item.Properties, T> func) {
        return ITEM.register(name, () -> func.apply(new Item.Properties().tab(ItemRegistry.ITEM_GROUP)));
    }

    private static <V extends Block> RegistryObject<BlockItem> regBlockItem(RegistryObject<V> b) {
        return regItem(b.getId().getPath(), p -> new BlockItem(b.get(), p));
    }

    @SuppressWarnings({"unchecked"})
    private static <T extends Item> RegistryObject<T>[] regArmor(String name, BiFunction<EquipmentSlotType, Item.Properties, T> func) {
        RegistryObject<T>[] ans = new RegistryObject[4];
        ans[0] = regItem(name + "helmet", (p) -> func.apply(EquipmentSlotType.HEAD, p));
        ans[1] = regItem(name + "chestplate", (p) -> func.apply(EquipmentSlotType.CHEST, p));
        ans[2] = regItem(name + "leggings", (p) -> func.apply(EquipmentSlotType.LEGS, p));
        ans[3] = regItem(name + "boots", (p) -> func.apply(EquipmentSlotType.FEET, p));
        return ans;
    }

}
