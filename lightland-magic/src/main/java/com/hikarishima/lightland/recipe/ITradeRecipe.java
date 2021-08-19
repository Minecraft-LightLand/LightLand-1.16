package com.hikarishima.lightland.recipe;

import com.lcy0x1.base.BaseRecipe;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ITradeRecipe<R extends ITradeRecipe<R>> extends BaseRecipe<R, ITradeRecipe<?>, ITradeRecipe.Inv> {

    public ITradeRecipe(ResourceLocation id, RecType<R, ITradeRecipe<?>, Inv> fac) {
        super(id, fac);
    }

    @Override
    public final boolean matches(Inv inv, World world) {
        return false;
    }

    @Override
    public final ItemStack assemble(Inv inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public final boolean canCraftInDimensions(int r, int c) {
        return false;
    }

    @Override
    public final ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    public interface Inv extends RecInv<ITradeRecipe<?>> {

    }


}
