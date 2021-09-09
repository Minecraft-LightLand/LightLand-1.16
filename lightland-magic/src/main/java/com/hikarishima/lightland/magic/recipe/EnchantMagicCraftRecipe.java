package com.hikarishima.lightland.magic.recipe;

import com.hikarishima.lightland.magic.registry.block.RitualCore;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@SerialClass
@ParametersAreNonnullByDefault
public class EnchantMagicCraftRecipe extends AbstractLevelCraftRecipe<EnchantMagicCraftRecipe> {

    public EnchantMagicCraftRecipe(ResourceLocation id) {
        super(id, MagicRecipeRegistry.RSM_ENCHANT.get());
    }

    public void assemble(RitualCore.Inv inv, int level) {
        ItemStack stack = assemble(inv);
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
        map.replaceAll((e, v) -> v + level - 1);
        EnchantmentHelper.setEnchantments(map, stack);
        inv.setItem(5, stack);
    }

}
