package com.hikarishima.lightland.magic.recipe;

import com.hikarishima.lightland.magic.registry.block.RitualCore;
import com.lcy0x1.base.BaseRecipe;
import com.lcy0x1.core.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SerialClass
public abstract class IMagicCraftRecipe<R extends IMagicCraftRecipe<R>> extends BaseRecipe<R, IMagicCraftRecipe<?>, RitualCore.Inv> {

    public IMagicCraftRecipe(ResourceLocation id, RecType<R, IMagicCraftRecipe<?>, RitualCore.Inv> fac) {
        super(id, fac);
    }

    @Override
    public boolean canCraftInDimensions(int r, int c) {
        return true;
    }

    @SerialClass
    public static class Entry {

        @SerialClass.SerialField
        public Ingredient input = Ingredient.EMPTY;

        @SerialClass.SerialField
        public ItemStack output = ItemStack.EMPTY;

    }

}
