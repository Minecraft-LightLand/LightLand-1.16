package com.hikarishima.lightland.magic.profession;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.lcy0x1.base.NamedEntry;

public abstract class Profession extends NamedEntry<Profession> {

    public Profession() {
        super(() -> MagicRegistry.PROFESSION);
    }

    public abstract void init(MagicHandler handler);

}
