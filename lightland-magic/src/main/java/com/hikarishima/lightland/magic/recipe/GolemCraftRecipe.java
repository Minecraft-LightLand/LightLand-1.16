package com.hikarishima.lightland.magic.recipe;

import com.google.gson.JsonObject;
import com.hikarishima.lightland.magic.registry.item.summon.GolemFrame;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GolemCraftRecipe extends ShapedRecipe {

    public GolemCraftRecipe(ResourceLocation rl, String str, int w, int h, NonNullList<Ingredient> list, ItemStack result) {
        super(rl, str, w, h, list, result);
    }

    @Override
    public ItemStack assemble(CraftingInventory inv) {
        ListNBT list = new ListNBT();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() instanceof GolemFrame) {
                CompoundNBT tag = stack.getTag();
                String mat = "";
                if (tag != null && tag.contains("item")) {
                    mat = tag.getString("item");
                }
                list.add(StringNBT.valueOf(mat));
            }
        }
        ItemStack result = super.assemble(inv);
        result.getOrCreateTag().put("materials", list);
        return result;
    }

    public static class Serializer extends ShapedRecipe.Serializer {

        public GolemCraftRecipe fromJson(ResourceLocation rl, JsonObject json) {
            ShapedRecipe r = super.fromJson(rl, json);
            return new GolemCraftRecipe(rl, r.getGroup(), r.getWidth(), r.getHeight(), r.getIngredients(), r.getResultItem());
        }

        @SuppressWarnings("ConstantConditions")
        public ShapedRecipe fromNetwork(ResourceLocation rl, PacketBuffer packet) {
            ShapedRecipe r = super.fromNetwork(rl, packet);
            return new GolemCraftRecipe(rl, r.getGroup(), r.getWidth(), r.getHeight(), r.getIngredients(), r.getResultItem());
        }

    }

}
