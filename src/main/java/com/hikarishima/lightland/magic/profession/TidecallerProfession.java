package com.hikarishima.lightland.magic.profession;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;

public class TidecallerProfession extends Profession {

    @Override
    public void init(MagicHandler handler) {
        handler.abilityPoints.general += 4;
        handler.magicHolder.addElementalMastery(MagicRegistry.ELEM_WATER);
        handler.magicHolder.addElementalMastery(MagicRegistry.ELEM_WATER);
        handler.abilityPoints.element++;
    }

    @Override
    public void levelUp(MagicHandler handler) {
        handler.abilityPoints.general++;
        if (handler.abilityPoints.level <= 10)
            handler.abilityPoints.general++;
        if (handler.abilityPoints.level % 2 == 0)
            handler.abilityPoints.element++;
    }

}
