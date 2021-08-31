package com.hikarishima.lightland.magic.products.instance;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.recipe.IMagicRecipe;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.lcy0x1.core.util.NBTObj;
import net.minecraft.util.ResourceLocation;

public class SpellMagic extends MagicProduct<Spell<?, ?>, SpellMagic> {

    public SpellMagic(MagicHandler player, NBTObj tag, ResourceLocation rl, IMagicRecipe<?> r) {
        super(MagicRegistry.MPT_SPELL, player, tag, rl, r);
    }

}
