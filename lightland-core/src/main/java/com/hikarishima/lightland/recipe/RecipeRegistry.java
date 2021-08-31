package com.hikarishima.lightland.recipe;

import com.hikarishima.lightland.LightLand;
import com.lcy0x1.base.BaseRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeRegistry {

    public static final DeferredRegister<IRecipeSerializer<?>> REC = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, LightLand.MODID);

    public static final IRecipeType<IConfigRecipe<?>> RT_CONFIG = IRecipeType.register("lightland-core:config");
    public static final IRecipeType<ITradeRecipe<?>> RT_TRADE = IRecipeType.register("lightland-core:trade");

    public static final RegistryObject<BaseRecipe.RecType<ConfigRecipe, IConfigRecipe<?>, IConfigRecipe.ConfigInv>> RSM_CONFIG =
            REC.register("config", () -> new BaseRecipe.RecType<>(ConfigRecipe.class, RT_CONFIG));

    public static final RegistryObject<BaseRecipe.RecType<TradeRecipe, ITradeRecipe<?>, ITradeRecipe.Inv>> RSM_TRADE =
            REC.register("trade", () -> new BaseRecipe.RecType<>(TradeRecipe.class, RT_TRADE));

}
