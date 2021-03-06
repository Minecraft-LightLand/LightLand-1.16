package com.hikarishima.lightland.magic.registry.item.magic;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.recipe.IMagicRecipe;
import com.hikarishima.lightland.magic.registry.item.combat.IGlowingTarget;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.hikarishima.lightland.proxy.Proxy;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicWand extends Item implements IGlowingTarget {

    public MagicWand(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrCreateTag().getString("recipe").length() > 0;
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

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        String str = stack.getOrCreateTag().getString("recipe");
        if (str.length() == 0)
            return ActionResult.pass(stack);
        MagicProduct<?, ?> p = getData(player, stack);
        stack.getOrCreateTag().remove("recipe");
        if (p == null) {
            return ActionResult.pass(stack);
        }
        if (p.type == MagicRegistry.MPT_SPELL) {
            Spell<?, ?> sp = (Spell<?, ?>) p.item;
            if (sp.attempt(Spell.Type.WAND, player.level, player))
                player.getCooldowns().addCooldown(this, 60);
        }
        return ActionResult.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        PlayerEntity pl = Proxy.getPlayer();
        if (world != null && pl != null) {
            MagicProduct<?, ?> p = getData(pl, stack);
            if (p != null) {
                list.add(new TranslationTextComponent(p.getDescriptionID()));
                if (p.type == MagicRegistry.MPT_SPELL) {
                    Spell<?, ?> spell = (Spell<?, ?>) p.item;
                    int cost = spell.getConfig(world, pl).mana_cost;
                    list.add(Translator.get("tooltip.mana_cost", cost));
                }
            }
        }
        super.appendHoverText(stack, world, list, flag);
    }

    public void setMagic(IMagicRecipe<?> recipe, ItemStack stack) {
        stack.getOrCreateTag().putString("recipe", recipe.id.toString());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getDistance(ItemStack stack) {
        MagicProduct<?, ?> prod = getData(Proxy.getClientPlayer(), stack);
        if (prod == null || prod.type != MagicRegistry.MPT_SPELL)
            return 0;
        Spell<?, ?> spell = (Spell<?, ?>) prod.item;
        return spell.getDistance(Proxy.getClientPlayer());
    }
}
