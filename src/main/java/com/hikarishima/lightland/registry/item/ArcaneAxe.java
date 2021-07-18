package com.hikarishima.lightland.registry.item;

import com.hikarishima.lightland.magic.arcane.internal.ArcaneItemUseHelper;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;

public class ArcaneAxe extends AxeItem {

    public ArcaneAxe(IItemTier tier, float attack, float speed, Properties props) {
        super(tier, attack, speed, props);
    }

    public boolean isFoil(ItemStack stack) {
        return ArcaneItemUseHelper.isAxeCharged(stack);
    }

}
