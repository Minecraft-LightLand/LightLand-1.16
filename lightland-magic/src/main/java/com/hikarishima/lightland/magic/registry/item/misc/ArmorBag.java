package com.hikarishima.lightland.magic.registry.item.misc;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArmorBag extends AbstractBag {

    public ArmorBag(Properties props) {
        super(props);
    }

    @Override
    public boolean matches(ItemStack self, ItemStack stack) {
        return stack.isDamageableItem() && !stack.getItem().getRegistryName().toString().equals("apotheosis:potion_charm");
    }


}
