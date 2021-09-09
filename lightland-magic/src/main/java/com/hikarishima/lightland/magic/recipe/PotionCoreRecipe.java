package com.hikarishima.lightland.magic.recipe;

import com.hikarishima.lightland.magic.registry.block.RitualCore;
import com.hikarishima.lightland.magic.registry.block.RitualSide;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SerialClass
@ParametersAreNonnullByDefault
public class PotionCoreRecipe extends AbstractMagicCraftRecipe<PotionCoreRecipe> {

    public PotionCoreRecipe(ResourceLocation id) {
        super(id, MagicRecipeRegistry.RSM_POTION_CORE.get());
    }

    @Override
    public void assemble(RitualCore.Inv inv, int level) {
        Map<Effect, EffectInstance> map = new HashMap<>();
        for (RitualSide.TE te : inv.sides) {
            ItemStack stack = te.getItem(0);
            if (stack.getItem() == Items.POTION) {
                for (EffectInstance ins : PotionUtils.getMobEffects(te.getItem(0))) {
                    map.put(ins.getEffect(), ins);
                }
            }
        }
        ItemStack stack = assemble(inv);
        PotionUtils.setCustomEffects(stack, new ArrayList<>(map.values()));
        inv.setItem(5, stack);
    }

}
