package com.hikarishima.lightland.magic.chem;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.text.ITextComponent;

@SerialClass
public class ChemElement extends ChemObj<ChemElement, MagicElement> {

    public ChemElement() {
        super(State.LIQUID);
    }

    @Override
    public MagicElement get() {
        if (!MagicRegistry.ELEMENT.containsKey(id))
            return null;
        return MagicRegistry.ELEMENT.getValue(id);
    }

    @Override
    public boolean known(MagicHandler handler) {
        return handler.magicHolder.getElementalMastery(get()) > 0;
    }

    @Override
    public ITextComponent getDesc() {
        return get().getDesc();
    }

}
