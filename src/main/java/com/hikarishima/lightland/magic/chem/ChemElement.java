package com.hikarishima.lightland.magic.chem;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.lcy0x1.core.util.SerialClass;

@SerialClass
public class ChemElement extends ChemObj<MagicElement> {

    public ChemElement() {
        super(State.LIQUID);
    }

    @Override
    public MagicElement get() {
        return MagicRegistry.ELEMENT.getValue(id);
    }

    @Override
    public boolean known(MagicHandler handler) {
        return handler.magicHolder.getElementalMastery(get()) > 0;
    }

}
