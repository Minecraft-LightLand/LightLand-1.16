package com.hikarishima.lightland.magic.profession;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;

public class ArcaneProfession extends Profession {

    @Override
    public void init(MagicHandler handler) {
        handler.abilityPoints.arcane += 3;
        handler.magicHolder.addElementalMastery(MagicRegistry.ELEM_VOID);
        handler.abilityPoints.element++;
    }

}
