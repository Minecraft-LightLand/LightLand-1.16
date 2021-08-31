package com.hikarishima.lightland.recipe;

import net.minecraft.util.ResourceLocation;

public class TradeRecipe extends ITradeRecipe<TradeRecipe> {

    public TradeRecipe(ResourceLocation id) {
        super(id, RecipeRegistry.RSM_TRADE.get());
    }

}
