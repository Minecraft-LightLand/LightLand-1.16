package com.hikarishima.lightland.recipe;

import com.hikarishima.lightland.LightLand;
import com.lcy0x1.base.BaseRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeRegistry {

    public static final IRecipeType<IConfigRecipe<?>> RT_CONFIG = IRecipeType.register("lightland-core:config");
    public static final IRecipeType<ITradeRecipe<?>> RT_TRADE = IRecipeType.register("lightland-core:trade");

    public static final BaseRecipe.RecType<ConfigRecipe, IConfigRecipe<?>, IConfigRecipe.ConfigInv> RSM_CONFIG =
            reg("config", new BaseRecipe.RecType<>(ConfigRecipe.class, RT_CONFIG));

    public static final BaseRecipe.RecType<TradeRecipe, ITradeRecipe<?>, ITradeRecipe.Inv> RSM_TRADE =
            reg("trade", new BaseRecipe.RecType<>(TradeRecipe.class, RT_TRADE));

    private static <V extends T, T extends IForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(new ResourceLocation(LightLand.MODID, name));
        return v;
    }

}
