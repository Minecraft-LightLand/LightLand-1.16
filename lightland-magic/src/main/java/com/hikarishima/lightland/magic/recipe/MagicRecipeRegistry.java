package com.hikarishima.lightland.magic.recipe;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.lcy0x1.base.BaseRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class MagicRecipeRegistry {

    public static final IRecipeType<IMagicRecipe<?>> RT_MAGIC = IRecipeType.register("lightland-magic:magic");

    public static final BaseRecipe.RecType<DefMagicRecipe, IMagicRecipe<?>, IMagicRecipe.Inv> RSM_DEF =
            reg("magic_default", new BaseRecipe.RecType<>(DefMagicRecipe.class, RT_MAGIC));

    public static final BaseRecipe.RecType<ShortMagicRecipe, IMagicRecipe<?>, IMagicRecipe.Inv> RSM_SHORT =
            reg("magic_short", new BaseRecipe.RecType<>(ShortMagicRecipe.class, RT_MAGIC));

    public static final ResourceLocation SPELL = new ResourceLocation(LightLandMagic.MODID, "config_spell");
    public static final ResourceLocation PRODUCT_TYPE_DISPLAY = new ResourceLocation(LightLandMagic.MODID, "config_product_type");
    public static final ResourceLocation CHEM = new ResourceLocation(LightLandMagic.MODID, "config_chemistry");
    public static final ResourceLocation SPELL_ENTITY = new ResourceLocation(LightLandMagic.MODID, "config_spell_entity");

    private static <V extends T, T extends IForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(new ResourceLocation(LightLandMagic.MODID, name));
        return v;
    }

}
