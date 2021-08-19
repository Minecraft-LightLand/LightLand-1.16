package com.hikarishima.lightland.magic.profession;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;

public abstract class SemiMagicProfession extends Profession {

    @Override
    public final void init(MagicHandler handler) {
        handler.abilityPoints.general += 2;
        handler.abilityPoints.magic += 2;
        handler.abilityPoints.element += 3;
    }

    @Override
    public final void levelUp(MagicHandler handler) {
        handler.abilityPoints.general++;
        if (handler.abilityPoints.level <= 10)
            handler.abilityPoints.magic++;
        handler.abilityPoints.element++;
    }

}
