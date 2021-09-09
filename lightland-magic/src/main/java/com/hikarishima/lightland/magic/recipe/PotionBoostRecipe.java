package com.hikarishima.lightland.magic.recipe;

import com.hikarishima.lightland.magic.registry.block.RitualCore;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@SerialClass
@ParametersAreNonnullByDefault
public class PotionBoostRecipe extends AbstractLevelCraftRecipe<PotionBoostRecipe> {

    @SerialClass.SerialField
    public ResourceLocation effect;

    @SerialClass.SerialField
    public int modify_level;

    public PotionBoostRecipe(ResourceLocation id) {
        super(id, MagicRecipeRegistry.RSM_POTION_BOOST.get());
    }

    public void assemble(RitualCore.Inv inv, int level) {
        ItemStack stack = inv.core.getItem(0).copy();
        List<EffectInstance> list = new ArrayList<>();
        for (EffectInstance ins : PotionUtils.getCustomEffects(stack)) {
            if (ins.getEffect().getRegistryName().equals(effect)) {
                if (ins.getAmplifier() < level) {
                    if (modify_level == 0)
                        continue;
                    list.add(new EffectInstance(ins.getEffect(), ins.getDuration(), level - 1));
                    continue;
                }
            }
            list.add(ins);
        }
        PotionUtils.setCustomEffects(stack, list);
        assemble(inv);
        inv.setItem(5, stack);
    }

}
