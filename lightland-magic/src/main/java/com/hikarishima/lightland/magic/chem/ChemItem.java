package com.hikarishima.lightland.magic.chem;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.recipe.IMagicRecipe;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.item.Item;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

@SerialClass
public class ChemItem extends ChemObj<ChemItem, Item> {

    public ChemItem() {
        super(State.SOLID);
    }

    @Override
    public Item get() {
        if (!ForgeRegistries.ITEMS.containsKey(id))
            return null;
        return ForgeRegistries.ITEMS.getValue(id);
    }

    @Override
    public boolean known(MagicHandler handler) {
        IMagicRecipe<?> r = IMagicRecipe.getMap(handler.world, MagicRegistry.MPT_CRAFT).get(get());
        return r == null || handler.magicHolder.getProduct(r).usable();
    }

    @Override
    public IFormattableTextComponent getDesc() {
        return new TranslationTextComponent(get().getDescriptionId());
    }
}
