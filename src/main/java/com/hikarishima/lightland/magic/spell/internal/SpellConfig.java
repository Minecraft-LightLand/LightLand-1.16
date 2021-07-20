package com.hikarishima.lightland.magic.spell.internal;

import com.hikarishima.lightland.registry.item.magic.MagicScroll;
import com.lcy0x1.core.util.SerialClass;

@SerialClass
public class SpellConfig {

    @SerialClass.SerialField
    public int duration, mana_cost, spell_load;

    @SerialClass.SerialField
    public MagicScroll.ScrollType type;

}
