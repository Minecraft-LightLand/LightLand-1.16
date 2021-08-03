package com.hikarishima.lightland.magic;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.products.MagicProductType;
import com.hikarishima.lightland.magic.products.instance.*;
import com.hikarishima.lightland.magic.profession.*;
import com.hikarishima.lightland.magic.skills.Skill;
import com.hikarishima.lightland.magic.spell.internal.AbstractSpell;
import com.lcy0x1.base.NamedEntry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@SuppressWarnings("unused")
public class MagicRegistry {

    public static final MagicElement ELEM_EARTH = reg("earth", new MagicElement());
    public static final MagicElement ELEM_AIR = reg("air", new MagicElement());
    public static final MagicElement ELEM_WATER = reg("water", new MagicElement());
    public static final MagicElement ELEM_FIRE = reg("fire", new MagicElement());
    public static final MagicElement ELEM_VOID = reg("quint", new MagicElement());

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

    public static IForgeRegistry<MagicElement> ELEMENT;
    public static IForgeRegistry<MPTRaw> PRODUCT_TYPE;
    public static IForgeRegistry<ArcaneType> ARCANE_TYPE;
    public static IForgeRegistry<Arcane> ARCANE;
    public static IForgeRegistry<AbstractSpell> SPELL;
    public static IForgeRegistry<Profession> PROFESSION;
    public static IForgeRegistry<Skill> SKILL;

    public static final MagicProductType<Enchantment, EnchantmentMagic> MPT_ENCH =
            reg("enchantment", new MagicProductType<>(
                    EnchantmentMagic.class, EnchantmentMagic::new,
                    () -> ForgeRegistries.ENCHANTMENTS, Enchantment::getDescriptionId));
    public static final MagicProductType<Effect, PotionMagic> MPT_EFF =
            reg("effect", new MagicProductType<>(
                    PotionMagic.class, PotionMagic::new,
                    () -> ForgeRegistries.POTIONS, Effect::getDescriptionId));
    public static final MagicProductType<Arcane, ArcaneMagic> MPT_ARCANE =
            reg("arcane", new MagicProductType<>(
                    ArcaneMagic.class, ArcaneMagic::new,
                    () -> ARCANE, Arcane::getDescriptionId));
    public static final MagicProductType<AbstractSpell, SpellMagic> MPT_SPELL =
            reg("spell", new MagicProductType<>(
                    SpellMagic.class, SpellMagic::new,
                    () -> SPELL, AbstractSpell::getDescriptionId));
    public static final MagicProductType<Item, CraftMagic> MPT_CRAFT =
            reg("craft", new MagicProductType<>(
                    CraftMagic.class, CraftMagic::new,
                    () -> ForgeRegistries.ITEMS, Item::getDescriptionId));

    public static void createRegistries() {
        ELEMENT = new RegistryBuilder<MagicElement>()
                .setName(new ResourceLocation(LightLand.MODID, "magic_element"))
                .setType(MagicElement.class).create();

        PRODUCT_TYPE = new RegistryBuilder<MPTRaw>()
                .setName(new ResourceLocation(LightLand.MODID, "magic_product_type"))
                .setType(MPTRaw.class).create();

        ARCANE_TYPE = new RegistryBuilder<ArcaneType>()
                .setName(new ResourceLocation(LightLand.MODID, "arcane_type"))
                .setType(ArcaneType.class).create();

        ARCANE = new RegistryBuilder<Arcane>()
                .setName(new ResourceLocation(LightLand.MODID, "arcane"))
                .setType(Arcane.class).create();

        SPELL = new RegistryBuilder<AbstractSpell>()
                .setName(new ResourceLocation(LightLand.MODID, "spell"))
                .setType(AbstractSpell.class).create();

        PROFESSION = new RegistryBuilder<Profession>()
                .setName(new ResourceLocation(LightLand.MODID, "profession"))
                .setType(Profession.class).create();

        SKILL = new RegistryBuilder<Skill>()
                .setName(new ResourceLocation(LightLand.MODID, "skill"))
                .setType(Skill.class).create();

    }

    private static <V extends T, T extends ForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(LightLand.MODID, name);
        return v;
    }

    public static abstract class MPTRaw extends NamedEntry<MPTRaw> {

        public MPTRaw() {
            super(() -> PRODUCT_TYPE);
        }

        @SuppressWarnings("unchecked")
        public <I extends ForgeRegistryEntry<I>, P extends MagicProduct<I, P>> MagicProductType<I, P> getAsType() {
            return (MagicProductType<I, P>) this;
        }

    }

}
