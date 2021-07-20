package com.hikarishima.lightland.magic.products;

import com.hikarishima.lightland.magic.MagicProduct;
import com.hikarishima.lightland.magic.MagicProductType;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.spell.Spell;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.lcy0x1.core.util.NBTObj;
import net.minecraft.util.ResourceLocation;

public class SpellMagic extends MagicProduct<Spell, SpellMagic> {

    public SpellMagic(MagicHandler player, NBTObj tag, ResourceLocation rl, IMagicRecipe<?> r) {
        super(MagicRegistry.MPT_SPELL, player, tag, rl, r);
    }

}
