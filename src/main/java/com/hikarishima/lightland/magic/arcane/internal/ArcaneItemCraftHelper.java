package com.hikarishima.lightland.magic.arcane.internal;

import com.hikarishima.lightland.magic.MagicRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ArcaneItemCraftHelper {

    public static Arcane getArcaneOnItem(ItemStack stack, ArcaneType type) {
        CompoundNBT tag = stack.getOrCreateTagElement("arcane");
        String s = type.getID();
        if (!tag.contains(s))
            return null;
        String str = tag.getString(s);
        ResourceLocation rl = new ResourceLocation(str);
        return MagicRegistry.ARCANE.getValue(rl);
    }

    public static List<Arcane> getAllArcanesOnItem(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTagElement("arcane");
        List<Arcane> list = new ArrayList<>();
        for (String str : tag.getAllKeys()) {
            list.add(MagicRegistry.ARCANE.getValue(new ResourceLocation(tag.getString(str))));
        }
        return list;
    }

    public static void setArcaneOnItem(ItemStack stack, Arcane arcane) {
        String s = arcane.type.getID();
        String str = arcane.getID();
        stack.getOrCreateTagElement("arcane").putString(s, str);
    }

}
