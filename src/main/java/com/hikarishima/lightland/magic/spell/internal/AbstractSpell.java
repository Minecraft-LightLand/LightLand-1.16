package com.hikarishima.lightland.magic.spell.internal;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.lcy0x1.base.NamedEntry;

public abstract class AbstractSpell extends NamedEntry<AbstractSpell> {

    public AbstractSpell() {
        super(() -> MagicRegistry.SPELL);
    }

    public Spell cast() {
        return (Spell) this;
    }

}
