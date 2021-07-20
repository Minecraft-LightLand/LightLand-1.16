package com.hikarishima.lightland.registry.item.magic;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicAbility;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.spell.internal.AbstractSpell;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.hikarishima.lightland.proxy.Proxy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MagicScroll extends Item {

    public MagicScroll(ScrollType type, Properties props) {
        super(type.apply(props));
    }

    public static void initItemStack(PlayerEntity crafter, Spell<?, ?> spell, ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTagElement("spell");
        tag.putString("user", crafter.getStringUUID());
        tag.putString("spell", spell.getID());
    }

    public static Spell<?, ?> getSpell(ItemStack stack) {
        String id = stack.getOrCreateTagElement("spell").getString("spell");
        if (id.length() == 0)
            return null;
        ResourceLocation rl = new ResourceLocation(id);
        AbstractSpell abs = MagicRegistry.SPELL.getValue(rl);
        if (abs == null)
            return null;
        return abs.cast();
    }

    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    public double getDurabilityForDisplay(ItemStack stack) {
        PlayerEntity pl = Proxy.getPlayer();
        if (pl == null)
            return 1;
        MagicAbility ability = MagicHandler.get(pl).magicAbility;
        int id = -1;
        for (int i = 0; i < ability.getMaxSpellSlot(); i++) {
            if (pl.inventory.getItem(i) == stack) {
                id = i;
                break;
            }
        }
        if (id == -1)
            return 1;
        return ability.getSpellActivation(id);

    }

    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return 0xFFFFFF;
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int selected = player.inventory.selected;
        MagicHandler handler = MagicHandler.get(player);
        if (handler.magicAbility.getMaxSpellSlot() <= selected)
            return ActionResult.fail(stack);
        if (player.inventory.getItem(selected) != stack)
            return ActionResult.fail(stack);
        if (handler.magicAbility.getSpellActivation(selected) != 0)
            return ActionResult.fail(stack);
        Spell<?, ?> spell = getSpell(stack);
        if (spell == null || !spell.attempt(Spell.Type.SCROLL, world, player))
            return ActionResult.fail(stack);
        player.getCooldowns().addCooldown(this, 10);
        if (!player.abilities.instabuild) {
            stack.shrink(1);
        }
        return ActionResult.sidedSuccess(stack, world.isClientSide());
    }

    public enum ScrollType {
        CARD(64), PARCHMENT(16), SCROLL(2);

        public final int stack;

        ScrollType(int stack) {
            this.stack = stack;
        }

        public Properties apply(Properties props) {
            props.stacksTo(stack);
            return props;
        }
    }

}
