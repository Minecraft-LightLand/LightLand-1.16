package com.hikarishima.lightland.magic.profession;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;

public abstract class CombatProfession extends Profession {

    @Override
    public final void init(MagicHandler handler) {
        handler.abilityPoints.general++;
        handler.abilityPoints.body += 3;
    }

    @Override
    public final void levelUp(MagicHandler handler) {
        handler.abilityPoints.general++;
        if (handler.abilityPoints.level <= 10)
            handler.abilityPoints.body++;
        if (handler.abilityPoints.level % 2 == 0)
            handler.abilityPoints.element++;
    }

}
