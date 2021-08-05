package com.hikarishima.lightland.registry.item.magic;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicWand extends Item {

    public MagicWand(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getTagElement("recipe") != null;
    }

    @Nullable
    public MagicProduct<?, ?> getData(PlayerEntity player, ItemStack stack) {
        String str = stack.getOrCreateTag().getString("recipe");
        if (str.length() == 0)
            return null;
        MagicHandler h = MagicHandler.get(player);
        IMagicRecipe<?> r = h.magicHolder.getRecipe(new ResourceLocation(str));
        MagicProduct<?, ?> p = h.magicHolder.getProduct(r);
        return p.usable() ? p : null;
    }

    public void activate(PlayerEntity player, IMagicRecipe<?> recipe, ItemStack stack) {
        MagicHandler handler = MagicHandler.get(player);
        MagicProduct<?, ?> p = handler.magicHolder.getProduct(recipe);
        if (p == null || !p.usable())
            return;
        if (p.type == MagicRegistry.MPT_SPELL) {
            Spell<?, ?> sp = (Spell<?, ?>) p.item;
            sp.attempt(Spell.Type.WAND, player.level, player);
        } else stack.getOrCreateTag().putString("recipe", recipe.id.toString());
    }
}
