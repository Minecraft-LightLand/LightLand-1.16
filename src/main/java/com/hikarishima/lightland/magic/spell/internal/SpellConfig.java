package com.hikarishima.lightland.magic.spell.internal;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.hikarishima.lightland.registry.item.magic.MagicScroll;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

@SerialClass
public class SpellConfig {

    @SerialClass.SerialField
    public int duration, mana_cost, spell_load;
    @SerialClass.SerialField
    public MagicScroll.ScrollType type;

    public static <C extends SpellConfig> C get(Spell<C, ?> spell, World world, PlayerEntity player) {
        C ans = ConfigRecipe.getObject(world, ConfigRecipe.SPELL, spell.getID());
        if (ans == null)
            return null;
        IMagicRecipe<?> r = IMagicRecipe.getMap(world, MagicRegistry.MPT_SPELL).get(spell);
        if (r == null)
            return ans;
        MagicProduct<?, ?> p = MagicHandler.get(player).magicHolder.getProduct(r);
        if (p == null || !p.usable())
            return ans;
        ans = makeCopy(ans);
        ans.mana_cost += p.getCost();
        ans.spell_load += p.getCost() * 2;
        return ans;
    }

    public static <C extends SpellConfig> C makeCopy(C config) {
        return Automator.fromTag(Automator.toTag(new CompoundNBT(), config), config.getClass());
    }

}
