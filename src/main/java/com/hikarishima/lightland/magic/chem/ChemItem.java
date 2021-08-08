package com.hikarishima.lightland.magic.chem;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.recipe.IMagicRecipe;
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

    @Override
    public boolean known(MagicHandler handler) {
        IMagicRecipe<?> r = IMagicRecipe.getMap(handler.world, MagicRegistry.MPT_CRAFT).get(get());
        return r == null || handler.magicHolder.getProduct(r).usable();
    }
}
