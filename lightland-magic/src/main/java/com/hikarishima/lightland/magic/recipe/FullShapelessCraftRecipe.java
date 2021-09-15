package com.hikarishima.lightland.magic.recipe;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FullShapelessCraftRecipe extends ShapelessRecipe {

    public FullShapelessCraftRecipe(ResourceLocation rl, String str, ItemStack stack, NonNullList<Ingredient> ingredients) {
        super(rl, str, stack, ingredients);
    }

    public boolean matches(CraftingInventory inv, World world) {
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).getDamageValue() > 0) {
                return false;
            }
        }
        return super.matches(inv, world);
    }

    public IRecipeSerializer<?> getSerializer() {
        return MagicRecipeRegistry.RSM_FULL_CRAFT.get();
    }

    public static class Serializer extends ShapelessRecipe.Serializer {

    }

}
