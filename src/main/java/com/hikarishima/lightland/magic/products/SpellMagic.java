package com.hikarishima.lightland.magic.products;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.spell.internal.AbstractSpell;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.lcy0x1.core.util.NBTObj;
import net.minecraft.util.ResourceLocation;

public class SpellMagic extends MagicProduct<AbstractSpell, SpellMagic> {

    public SpellMagic(MagicHandler player, NBTObj tag, ResourceLocation rl, IMagicRecipe<?> r) {
        super(MagicRegistry.MPT_SPELL, player, tag, rl, r);
    }

}
