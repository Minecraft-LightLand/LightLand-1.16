package com.hikarishima.lightland.magic.recipe;

import com.hikarishima.lightland.magic.registry.block.RitualCore;
import com.lcy0x1.base.BaseRecipe;
import com.lcy0x1.core.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SerialClass
public class MagicCraftRecipe extends BaseRecipe<MagicCraftRecipe, MagicCraftRecipe, RitualCore.Inv> {


    @SerialClass.SerialField
    public Entry core;

    @SerialClass.SerialField(generic = Entry.class)
    public ArrayList<Entry> side;


    public MagicCraftRecipe(ResourceLocation id) {
        super(id, MagicRecipeRegistry.RSM_CRAFT);
    }

    @Override
    public boolean matches(RitualCore.Inv inv, World world) {
        if (!core.input.test(inv.getItem(5)))
            return false;
        List<Entry> temp = side.stream().filter(e -> !e.input.isEmpty()).collect(Collectors.toList());
        for (int i = 0; i < 9; i++) {
            if (i == 5)
                continue;
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                Optional<Entry> entry = temp.stream().filter(e -> e.input.test(stack)).findFirst();
                if (!entry.isPresent())
                    return false;
                temp.remove(entry.get());
            }
        }
        return temp.isEmpty();
    }

    @Override
    public ItemStack assemble(RitualCore.Inv inv) {
        if (!core.input.test(inv.getItem(5)))
            return ItemStack.EMPTY;
        inv.setItem(5, core.output.copy());
        List<Entry> temp = side.stream().filter(e -> !e.input.isEmpty()).collect(Collectors.toList());
        for (int i = 0; i < 9; i++) {
            if (i == 5)
                continue;
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                Optional<Entry> entry = temp.stream().filter(e -> e.input.test(stack)).findFirst();
                if (!entry.isPresent())
                    return ItemStack.EMPTY;
                temp.remove(entry.get());
                inv.setItem(i, entry.get().output.copy());
            }
        }
        return core.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int r, int c) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return core.output;
    }

    @SerialClass
    public static class Entry {

        @SerialClass.SerialField
        public Ingredient input = Ingredient.EMPTY;

        @SerialClass.SerialField
        public ItemStack output = ItemStack.EMPTY;

    }

}
