package com.hikarishima.lightland.magic.profession;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;

public class SpellCasterProfession extends Profession {

    @Override
    public void init(MagicHandler handler) {
        handler.magicHolder.addElementalMastery(MagicRegistry.ELEM_EARTH);
        handler.magicHolder.addElementalMastery(MagicRegistry.ELEM_WATER);
        handler.magicHolder.addElementalMastery(MagicRegistry.ELEM_AIR);
        handler.magicHolder.addElementalMastery(MagicRegistry.ELEM_FIRE);
    }

    @Override
    public void levelUp(MagicHandler handler) {
        handler.abilityPoints.general++;
        handler.abilityPoints.magic++;
    }

}
