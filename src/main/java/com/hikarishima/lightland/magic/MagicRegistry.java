package com.hikarishima.lightland.magic;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.products.EnchantmentMagic;
import com.hikarishima.lightland.magic.products.PotionMagic;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

public class MagicRegistry {

    public static abstract class MPTRaw extends ForgeRegistryEntry<MPTRaw> {

        @SuppressWarnings("unchecked")
        public <I extends ForgeRegistryEntry<I>, P extends MagicProduct<I, P>> MagicProductType<I, P> getAsType() {
            return (MagicProductType<I, P>) this;
        }

    }

    public static IForgeRegistry<MagicElement> ELEMENT;
    public static IForgeRegistry<MPTRaw> PRODUCT_TYPE;

    public static MagicElement ELEM_EARTH = reg("earth", new MagicElement());
    public static MagicElement ELEM_AIR = reg("air", new MagicElement());
    public static MagicElement ELEM_WATER = reg("water", new MagicElement());
    public static MagicElement ELEM_FIRE = reg("fire", new MagicElement());
    public static MagicElement ELEM_VOID = reg("void", new MagicElement());

    public static MagicProductType<Enchantment, EnchantmentMagic> MPT_ENCH =
            reg("enchantment", new MagicProductType<>(
                    EnchantmentMagic.class, EnchantmentMagic::new,
                    ForgeRegistries.ENCHANTMENTS::getValue, Enchantment::getDescriptionId,
                    Enchantments.ALL_DAMAGE_PROTECTION));


    public static MagicProductType<Effect, PotionMagic> MPT_EFF =
            reg("effect", new MagicProductType<>(
                    PotionMagic.class, PotionMagic::new,
                    ForgeRegistries.POTIONS::getValue, Effect::getDescriptionId,
                    Effects.MOVEMENT_SPEED));


    public static void createRegistries() {
        ELEMENT = new RegistryBuilder<MagicElement>()
                .setName(new ResourceLocation(LightLand.MODID, "magic_element"))
                .setType(MagicElement.class).create();

        PRODUCT_TYPE = new RegistryBuilder<MPTRaw>()
                .setName(new ResourceLocation(LightLand.MODID, "magic_product_type"))
                .setType(MPTRaw.class).create();

    }

    private static <V extends T, T extends ForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(LightLand.MODID, name);
        return v;
    }

}