package com.hikarishima.lightland.magic.profession;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;

public class ArcaneProfession extends Profession {

    @Override
    public void init(MagicHandler handler) {
        handler.abilityPoints.arcane += 2;
        handler.abilityPoints.general += 2;
        handler.magicHolder.addElementalMastery(MagicRegistry.ELEM_VOID);
        handler.abilityPoints.element++;
    }

    @Override
    public void levelUp(MagicHandler handler) {
        handler.abilityPoints.general++;
        if (handler.abilityPoints.level <= 10)
            handler.abilityPoints.general++;
        handler.abilityPoints.element++;
    }

}
