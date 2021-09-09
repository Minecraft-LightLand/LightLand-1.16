package com.hikarishima.lightland.magic.recipe;

import com.hikarishima.lightland.magic.registry.block.RitualCore;
import com.hikarishima.lightland.magic.registry.item.magic.MagicScroll;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@SerialClass
@ParametersAreNonnullByDefault
public class PotionModifyRecipe extends AbstractMagicCraftRecipe<PotionModifyRecipe> {

    public PotionModifyRecipe(ResourceLocation id) {
        super(id, MagicRecipeRegistry.RSM_POTION_MODIFY.get());
    }

    @Override
    public void assemble(RitualCore.Inv inv, int level) {
        ItemStack core = inv.core.getItem(0).copy();
        assemble(inv);
        if (target != null) {
            MagicScroll.setTarget(target, core);
        }
        if (radius > 0) {
            MagicScroll.setRadius(radius, core);
        }
        inv.setItem(5, core);
    }

    @SerialClass.SerialField
    public MagicScroll.TargetType target;

    @SerialClass.SerialField
    public double radius;

}
