package com.hikarishima.lightland.magic.recipe;

import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import com.hikarishima.lightland.magic.registry.block.RitualCore;
import com.hikarishima.lightland.magic.registry.block.RitualSide;
import com.hikarishima.lightland.magic.registry.item.magic.MagicScroll;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@SerialClass
@ParametersAreNonnullByDefault
public class PotionSpellRecipe extends AbstractMagicCraftRecipe<PotionSpellRecipe> {

    public PotionSpellRecipe(ResourceLocation id) {
        super(id, MagicRecipeRegistry.RSM_POTION_SPELL.get());
    }

    @Override
    public void assemble(RitualCore.Inv inv, int level) {
        ItemStack core = inv.core.getItem(0);
        List<EffectInstance> list = PotionUtils.getCustomEffects(core);
        MagicScroll.TargetType target = MagicScroll.getTarget(core);
        double radius = MagicScroll.getRadius(core);
        inv.setItem(5, assemble(inv));
        for (RitualSide.TE te : inv.sides) {
            ItemStack stack = te.getItem(0);
            if (stack.getItem() == MagicItemRegistry.SPELL_CARD.get()) {
                MagicScroll.initEffect(list, stack);
                MagicScroll.setTarget(target, stack);
                MagicScroll.setRadius(radius, stack);
            }
        }
    }

}
