package com.hikarishima.lightland.recipe;

import com.lcy0x1.base.BaseRecipe;
import com.lcy0x1.core.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@SerialClass
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IConfigRecipe<R extends IConfigRecipe<R>> extends BaseRecipe<R, IConfigRecipe<?>, IConfigRecipe.ConfigInv> {

    public IConfigRecipe(ResourceLocation id, RecType<R, IConfigRecipe<?>, ConfigInv> fac) {
        super(id, fac);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends IConfigRecipe<T>> T getRecipe(World world, Class<T> cls, ResourceLocation rl) {
        List<IConfigRecipe<?>> list = world.getRecipeManager().getAllRecipesFor(RecipeRegistry.RT_CONFIG);
        for (IConfigRecipe<?> r : list) {
            if (cls.isInstance(r) && r.id.equals(rl))
                return (T) r;
        }
        return null;
    }

    @Override
    public boolean matches(ConfigInv inv, World world) {
        return false;
    }

    @Override
    public ItemStack assemble(ConfigInv inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int r, int c) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    public interface ConfigInv extends RecInv<IConfigRecipe<?>> {

    }

}
