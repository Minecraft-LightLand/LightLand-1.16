package com.hikarishima.lightland.registry.item.magic;

import com.hikarishima.lightland.event.forge.ItemUseEventHandler;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicAbility;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.spell.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class MagicScroll extends Item implements ItemUseEventHandler.ItemClickHandler {

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

    public static void initItemStack(PlayerEntity crafter, Spell spell, ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTagElement("spell");
        tag.putString("user", crafter.getStringUUID());
        tag.putString("spell", spell.getID());
    }

    @OnlyIn(Dist.CLIENT)
    public static PlayerEntity getCrafterForRender(ItemStack stack) {
        String id = stack.getOrCreateTagElement("spell").getString("user");
        if (id.length() == 0)
            return null;
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null && player.getStringUUID().equals(id))
            return player;
        return null;
    }

    public static Spell getSpell(ItemStack stack) {
        String id = stack.getOrCreateTagElement("spell").getString("spell");
        if (id.length() == 0)
            return null;
        ResourceLocation rl = new ResourceLocation(id);
        return MagicRegistry.SPELL.getValue(rl);
    }

    public MagicScroll(ScrollType type, Properties props) {
        super(type.apply(props));
    }

    @Override
    public boolean predicate(ItemStack stack, Class<? extends PlayerEvent> cls, PlayerEvent event) {
        PlayerEntity player = event.getPlayer();
        int selected = player.inventory.selected;
        MagicHandler handler = MagicHandler.get(player);
        if (handler.magicAbility.getMaxSpellSlot() <= selected)
            return false;
        if (player.inventory.getItem(selected) != stack)
            return false;
        return handler.magicAbility.getSpellActivation(selected) == 0;
    }


    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    public double getDurabilityForDisplay(ItemStack stack) {
        PlayerEntity pl = getCrafterForRender(stack);
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

}
