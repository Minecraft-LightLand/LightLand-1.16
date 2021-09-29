package com.hikarishima.lightland.magic.recipe.vanilla;

import com.google.gson.JsonObject;
import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
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

        public ShapelessRecipe fromJson(ResourceLocation res, JsonObject json) {
            ShapelessRecipe r = super.fromJson(res, json);
            return new FullShapelessCraftRecipe(r.getId(), r.getGroup(), r.getResultItem(), r.getIngredients());
        }

        public ShapelessRecipe fromNetwork(ResourceLocation res, PacketBuffer buffer) {
            ShapelessRecipe r = super.fromNetwork(res, buffer);
            return r == null ? null : new FullShapelessCraftRecipe(r.getId(), r.getGroup(), r.getResultItem(), r.getIngredients());
        }

    }

}
