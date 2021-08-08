package com.hikarishima.lightland.magic.chem;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

@SerialClass
public class ChemItem extends ChemObj<Item> {

    public ChemItem() {
        super(State.SOLID);
    }

    @Override
    public Item get() {
        return ForgeRegistries.ITEMS.getValue(id);
    }
}
