package com.hikarishima.lightland.magic.products.info;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SerialClass
public class TypeConfig {

    @SerialClass.SerialField
    public Item icon;

    @SerialClass.SerialField
    public ResourceLocation background;

    public ItemStack getIcon() {
        return icon.getDefaultInstance();
    }

    public ResourceLocation getBackground() {
        return background;
    }

}
