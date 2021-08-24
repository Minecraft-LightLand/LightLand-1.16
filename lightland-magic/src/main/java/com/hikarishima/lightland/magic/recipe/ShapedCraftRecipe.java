package com.hikarishima.lightland.magic.recipe;

import com.hikarishima.lightland.magic.registry.block.RitualCore;
import com.lcy0x1.core.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ShapedCraftRecipe extends IMagicCraftRecipe<ShapedCraftRecipe> {

    @SerialClass.SerialField
    public String[] pattern;

    @SerialClass.SerialField
    public HashMap<String, Entry> key;


    public ShapedCraftRecipe(ResourceLocation id) {
        super(id, MagicRecipeRegistry.RSM_SHAPED);
    }

    @Override
    public boolean matches(RitualCore.Inv inv, World world) {
        //TODO implement
        return false;
    }

    @Override
    public ItemStack assemble(RitualCore.Inv inv) {
        //TODO implement
        return getResultItem().copy();
    }

    @Override
    public ItemStack getResultItem() {
        return key.get(pattern[1].charAt(1) + "").output;
    }

}
