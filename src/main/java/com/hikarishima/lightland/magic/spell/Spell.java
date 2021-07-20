package com.hikarishima.lightland.magic.spell;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.lcy0x1.base.NamedEntry;

public class Spell extends NamedEntry<Spell> {

    public Spell() {
        super(() -> MagicRegistry.SPELL);
    }
}
