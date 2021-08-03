package com.hikarishima.lightland.magic.profession;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;

public class BurserkerProfession extends Profession {

    @Override
    public void init(MagicHandler handler) {
        handler.abilityPoints.general++;
        handler.abilityPoints.body += 3;
    }

    @Override
    public void levelUp(MagicHandler handler) {
        handler.abilityPoints.general++;
        handler.abilityPoints.body++;
    }

}
