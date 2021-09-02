package com.hikarishima.lightland.magic.recipe;

import com.hikarishima.lightland.magic.registry.block.RitualCore;
import com.lcy0x1.base.BaseRecipe;
import com.lcy0x1.core.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SerialClass
public class AbstractMagicCraftRecipe<R extends AbstractMagicCraftRecipe<R>> extends BaseRecipe<R, AbstractMagicCraftRecipe<?>, RitualCore.Inv> {

    @SerialClass.SerialField
    public Entry core;

    @SerialClass.SerialField(generic = Entry.class)
    public ArrayList<Entry> side;

    public AbstractMagicCraftRecipe(ResourceLocation id, RecType<R, AbstractMagicCraftRecipe<?>, RitualCore.Inv> fac) {
        super(id, fac);
    }

    @Override
    public boolean matches(RitualCore.Inv inv, World world) {
        if (!core.test(inv.getItem(5)))
            return false;
        List<Entry> temp = side.stream().filter(e -> !e.input.isEmpty()).collect(Collectors.toList());
        for (int i = 0; i < 9; i++) {
            if (i == 5)
                continue;
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                Optional<Entry> entry = temp.stream().filter(e -> e.test(stack)).findFirst();
                if (!entry.isPresent())
                    return false;
                temp.remove(entry.get());
            }
        }
        return temp.isEmpty();
    }

    @Deprecated
    @Override
    public ItemStack assemble(RitualCore.Inv inv) {
        if (!core.test(inv.getItem(5)))
            return ItemStack.EMPTY;
        ItemStack ans = core.output.copy();
        List<Entry> temp = side.stream().filter(e -> !e.input.isEmpty()).collect(Collectors.toList());
        for (int i = 0; i < 9; i++) {
            if (i == 5)
                continue;
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                Optional<Entry> entry = temp.stream().filter(e -> e.test(stack)).findFirst();
                if (!entry.isPresent())
                    return ItemStack.EMPTY;
                temp.remove(entry.get());
                inv.setItem(i, entry.get().output.copy());
            }
        }
        return ans;
    }

    public void assemble(RitualCore.Inv inv, int level) {
        inv.setItem(5, assemble(inv));
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
        public ItemStack input = ItemStack.EMPTY;

        @SerialClass.SerialField
        public ItemStack output = ItemStack.EMPTY;

        public boolean test(ItemStack stack) {
            if (input.getItem() == Items.ENCHANTED_BOOK) {
                if (stack.getItem() != input.getItem())
                    return false;
                for (Map.Entry<Enchantment, Integer> ent : EnchantmentHelper.getEnchantments(input).entrySet()) {
                    Integer i = EnchantmentHelper.getEnchantments(stack).get(ent.getKey());
                    if (i == null || i < ent.getValue())
                        return false;
                }
                return true;
            }
            return stack.getItem() == input.getItem();
        }
    }

    @Nullable
    public ResourceLocation getMagic() {
        return null;
    }

    public int getLevel(int cost) {
        return 1;
    }

    public int getNextLevel(int cost) {
        return 0;
    }

}
