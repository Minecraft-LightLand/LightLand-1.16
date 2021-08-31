package com.hikarishima.lightland.magic;

import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.products.MagicProductType;
import com.hikarishima.lightland.magic.products.instance.*;
import com.hikarishima.lightland.magic.profession.*;
import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.magic.registry.MagicContainerRegistry;
import com.hikarishima.lightland.magic.registry.MagicEntityRegistry;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import com.hikarishima.lightland.magic.registry.VanillaMagicRegistry;
import com.hikarishima.lightland.magic.skills.Skill;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@SuppressWarnings("unused")
public class MagicRegistry {

    public static IForgeRegistry<MagicElement> ELEMENT;
    public static IForgeRegistry<MagicProductType<?, ?>> PRODUCT_TYPE;
    public static IForgeRegistry<ArcaneType> ARCANE_TYPE;
    public static IForgeRegistry<Arcane> ARCANE;
    public static IForgeRegistry<Spell<?, ?>> SPELL;
    public static IForgeRegistry<Profession> PROFESSION;
    public static IForgeRegistry<Skill> SKILL;

    public static final MagicElement ELEM_EARTH = reg("earth", new MagicElement());
    public static final MagicElement ELEM_AIR = reg("air", new MagicElement());
    public static final MagicElement ELEM_WATER = reg("water", new MagicElement());
    public static final MagicElement ELEM_FIRE = reg("fire", new MagicElement());
    public static final MagicElement ELEM_QUINT = reg("quint", new MagicElement());

    public static final ArcaneProfession PROF_ARCANE = reg("arcane", new ArcaneProfession());
    public static final MagicianProfession PROF_MAGIC = reg("magician", new MagicianProfession());
    public static final SpellCasterProfession PROF_SPELL = reg("spell_caster", new SpellCasterProfession());
    public static final KnightProfession PROF_KNIGHT = reg("knight", new KnightProfession());
    public static final ShielderProfession PROF_SHIELDER = reg("shielder", new ShielderProfession());
    public static final BurserkerProfession PROF_BURSERKER = reg("burserker", new BurserkerProfession());
    public static final ArcherProfession PROF_ARCHER = reg("archer", new ArcherProfession());
    public static final HunterProfession PROF_HUNTER = reg("hunter", new HunterProfession());
    public static final AlchemistProfession PROF_ALCHEM = reg("alchemist", new AlchemistProfession());
    public static final ChemistProfession PROF_CHEM = reg("chemist", new ChemistProfession());
    public static final TidecallerProfession PROF_TIDE = reg("tidecaller", new TidecallerProfession());
    public static final AssassinProfession PROF_ASSASSIN = reg("assassin", new AssassinProfession());

    public static final MagicProductType<Enchantment, EnchantmentMagic> MPT_ENCH =
            reg("enchantment", new MagicProductType<>(EnchantmentMagic.class, EnchantmentMagic::new,
                    () -> ForgeRegistries.ENCHANTMENTS, Enchantment::getDescriptionId, ELEM_AIR));
    public static final MagicProductType<Effect, PotionMagic> MPT_EFF =
            reg("effect", new MagicProductType<>(PotionMagic.class, PotionMagic::new,
                    () -> ForgeRegistries.POTIONS, Effect::getDescriptionId, ELEM_WATER));
    public static final MagicProductType<Arcane, ArcaneMagic> MPT_ARCANE =
            reg("arcane", new MagicProductType<>(ArcaneMagic.class, ArcaneMagic::new,
                    () -> ARCANE, Arcane::getDescriptionId, ELEM_QUINT));
    public static final MagicProductType<Spell<?, ?>, SpellMagic> MPT_SPELL =
            reg("spell", new MagicProductType<>(SpellMagic.class, SpellMagic::new,
                    () -> SPELL, Spell::getDescriptionId, ELEM_FIRE));
    public static final MagicProductType<Item, CraftMagic> MPT_CRAFT =
            reg("craft", new MagicProductType<>(CraftMagic.class, CraftMagic::new,
                    () -> ForgeRegistries.ITEMS, Item::getDescriptionId, ELEM_EARTH));

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void createRegistries() {
        ELEMENT = new RegistryBuilder<MagicElement>()
                .setName(new ResourceLocation(LightLandMagic.MODID, "magic_element"))
                .setType(MagicElement.class).create();

        PRODUCT_TYPE = new RegistryBuilder()
                .setName(new ResourceLocation(LightLandMagic.MODID, "magic_product_type"))
                .setType(MagicProductType.class).create();

        ARCANE_TYPE = new RegistryBuilder<ArcaneType>()
                .setName(new ResourceLocation(LightLandMagic.MODID, "arcane_type"))
                .setType(ArcaneType.class).create();

        ARCANE = new RegistryBuilder<Arcane>()
                .setName(new ResourceLocation(LightLandMagic.MODID, "arcane"))
                .setType(Arcane.class).create();

        SPELL = new RegistryBuilder()
                .setName(new ResourceLocation(LightLandMagic.MODID, "spell"))
                .setType(Spell.class).create();

        PROFESSION = new RegistryBuilder<Profession>()
                .setName(new ResourceLocation(LightLandMagic.MODID, "profession"))
                .setType(Profession.class).create();

        SKILL = new RegistryBuilder<Skill>()
                .setName(new ResourceLocation(LightLandMagic.MODID, "skill"))
                .setType(Skill.class).create();

    }

    private static <V extends T, T extends ForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(LightLandMagic.MODID, name);
        return v;
    }

    public static void initAllRegistries(IEventBus bus) {
        VanillaMagicRegistry.EFFECT.register(bus);
        VanillaMagicRegistry.ENCH.register(bus);
        MagicEntityRegistry.ENTITY.register(bus);
        MagicContainerRegistry.TE.register(bus);
        MagicContainerRegistry.CT.register(bus);
        MagicItemRegistry.ITEM.register(bus);
        MagicItemRegistry.BLOCK.register(bus);
        MagicRecipeRegistry.REC.register(bus);
    }

}
