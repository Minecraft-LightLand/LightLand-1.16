package com.hikarishima.lightland.item.arcane;

import com.hikarishima.lightland.item.arcane.internal.ArcaneItemUseHelper;
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
