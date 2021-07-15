package com.hikarishima.lightland.recipe;

import com.hikarishima.lightland.LightLand;
import com.lcy0x1.base.BaseRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeRegistry {

    public static final IRecipeType<IMagicRecipe<?>> RT_MAGIC = IRecipeType.register("lightland:magic");

    public static final BaseRecipe.RecType<DefMagicRecipe, IMagicRecipe<?>, IMagicRecipe.Inv> RSM_DEF =
            reg("magic_default", new BaseRecipe.RecType<>(DefMagicRecipe.class, RT_MAGIC));

    public static final BaseRecipe.RecType<ShortMagicRecipe, IMagicRecipe<?>, IMagicRecipe.Inv> RSM_SHORT =
            reg("magic_short", new BaseRecipe.RecType<>(ShortMagicRecipe.class, RT_MAGIC));

    private static <V extends T, T extends IForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(new ResourceLocation(LightLand.MODID, name));
        return v;
    }

}
