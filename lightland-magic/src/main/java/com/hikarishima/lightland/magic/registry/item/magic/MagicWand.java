package com.hikarishima.lightland.magic.registry.item.magic;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.recipe.IMagicRecipe;
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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicWand extends Item {

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
    public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity player, Hand hand) {
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
            sp.attempt(Spell.Type.WAND, player.level, player);
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
            }
        }
        super.appendHoverText(stack, world, list, flag);
    }

    public void setMagic(IMagicRecipe<?> recipe, ItemStack stack) {
        stack.getOrCreateTag().putString("recipe", recipe.id.toString());
    }
}
