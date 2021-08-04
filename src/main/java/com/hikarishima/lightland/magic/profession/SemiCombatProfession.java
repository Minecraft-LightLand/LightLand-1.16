package com.hikarishima.lightland.magic.profession;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;

public abstract class SemiCombatProfession extends Profession {

    @Override
    public final void init(MagicHandler handler) {
        handler.abilityPoints.general += 2;
        handler.abilityPoints.body += 2;
        handler.abilityPoints.element++;
    }

    @Override
    public final void levelUp(MagicHandler handler) {
        handler.abilityPoints.general++;
        if (handler.abilityPoints.level <= 100)
            handler.abilityPoints.body++;
        if (handler.abilityPoints.level % 2 == 0)
            handler.abilityPoints.element++;
    }

}
