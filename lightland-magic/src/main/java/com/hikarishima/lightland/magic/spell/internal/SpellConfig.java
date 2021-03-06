package com.hikarishima.lightland.magic.spell.internal;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.recipe.IMagicRecipe;
import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.magic.registry.item.magic.MagicScroll;
import com.hikarishima.lightland.recipe.ConfigRecipe;
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
    public float factor = 1f;
    @SerialClass.SerialField
    public MagicScroll.ScrollType type;

    public static <C extends SpellConfig> C get(Spell<C, ?> spell, World world, PlayerEntity player) {
        C ans = ConfigRecipe.getObject(world, MagicRecipeRegistry.SPELL, spell.getID());
        if (ans == null)
            return null;
        IMagicRecipe<?> r = IMagicRecipe.getMap(world, MagicRegistry.MPT_SPELL).get(spell);
        if (r == null)
            return ans;
        MagicProduct<?, ?> p = MagicHandler.get(player).magicHolder.getProduct(r);
        if (p == null || !p.usable())
            return ans;
        ans = makeCopy(ans);
        ans.mana_cost += p.getCost() * ans.factor;
        ans.spell_load += p.getCost() * ans.factor;
        return ans;
    }

    public static <C extends SpellConfig> C makeCopy(C config) {
        return Automator.fromTag(Automator.toTag(new CompoundNBT(), config), config.getClass());
    }

    @SerialClass
    public static class SpellDisplay {

        @SerialClass.SerialField
        public String id;

        @SerialClass.SerialField
        public int duration, setup, close;

    }

}
