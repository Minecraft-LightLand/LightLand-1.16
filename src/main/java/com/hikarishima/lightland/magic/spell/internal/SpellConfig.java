package com.hikarishima.lightland.magic.spell.internal;

import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.hikarishima.lightland.registry.item.magic.MagicScroll;
import com.lcy0x1.core.util.SerialClass;

@SerialClass
public class SpellConfig {

    @SerialClass.SerialField
    public int duration, mana_cost, spell_load;
    @SerialClass.SerialField
    public MagicScroll.ScrollType type;

    public static <C extends SpellConfig> C get(Spell<C, ?> spell, ActivationConfig act) {
        return ConfigRecipe.getObject(act.world, ConfigRecipe.SPELL, spell.getID());
    }

}
