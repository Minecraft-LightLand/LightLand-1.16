package com.hikarishima.lightland.magic.skills;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.lcy0x1.base.NamedEntry;

public class Skill extends NamedEntry<Skill> {

    public Skill() {
        super(() -> MagicRegistry.SKILL);
    }

}
