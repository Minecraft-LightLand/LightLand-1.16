package com.hikarishima.lightland.magic.gui.container;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.hikarishima.lightland.magic.spell.internal.SpellConfig;
import com.hikarishima.lightland.registry.ContainerRegistry;
import com.hikarishima.lightland.registry.item.magic.MagicScroll;
import com.hikarishima.lightland.registry.item.magic.MagicWand;
import com.hikarishima.lightland.registry.item.magic.ManaStorage;
import com.lcy0x1.core.util.SpriteManager;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SpellCraftContainer extends AbstractContainer {

    public static final SpriteManager MANAGER = new SpriteManager(LightLand.MODID, "spell_craft");

    protected Error err = Error.NO_ITEM;
    protected Spell<?, ?> spell = null;
    protected int consume = 0, total_cost = 0, available = 0;

    public SpellCraftContainer(int wid, PlayerInventory plInv) {
        super(ContainerRegistry.CT_SPELL_CRAFT, wid, plInv, 5, MANAGER);
        addSlot("wand_slot", stack -> stack.getItem() instanceof MagicWand);
        addSlot("input_slot", stack -> stack.getItem() instanceof MagicScroll);
        addSlot("ench_slot", stack -> stack.getItem() instanceof ManaStorage);
        addSlot("output_slot", stack -> false);
        addSlot("gold_slot", stack -> false);
    }

    @Override
    public void slotsChanged(IInventory inv) {
        err = check();
        super.slotsChanged(inv);
    }

    private Error check() {
        spell = null;
        consume = 0;
        total_cost = 0;
        available = 0;
        ItemStack wand = slot.getItem(0);
        ItemStack input = slot.getItem(1);
        ItemStack ench = slot.getItem(2);
        ItemStack output = slot.getItem(3);
        ItemStack gold = slot.getItem(4);
        if (wand.isEmpty() || input.isEmpty() || !output.isEmpty()) {
            return Error.NO_ITEM;
        }
        if (!(wand.getItem() instanceof MagicWand)) {
            return Error.NO_SPELL;
        }
        MagicWand magicWand = (MagicWand) wand.getItem();
        MagicProduct<?, ?> prod = magicWand.getData(plInv.player, wand);
        if (prod == null || prod.type != MagicRegistry.MPT_SPELL) {
            return Error.NO_SPELL;
        }
        if (!prod.usable()) {
            return Error.NOT_UNLOCKED;
        }
        spell = (Spell<?, ?>) prod.item;
        SpellConfig config = spell.getConfig(plInv.player.level, plInv.player);
        int cost = config.mana_cost;
        MagicScroll.ScrollType type = config.type;
        if (!(input.getItem() instanceof MagicScroll)) {
            return Error.WRONG_SCROLL;
        }
        MagicScroll magicScroll = (MagicScroll) input.getItem();
        if (magicScroll.type != type) {
            return Error.WRONG_SCROLL;
        }
        int count = input.getCount();
        total_cost = count * cost;
        if (ench.isEmpty() || !(ench.getItem() instanceof ManaStorage)) {
            return Error.NOT_ENOUGH_MANA;
        }
        int mana = ((ManaStorage) ench.getItem()).mana;
        consume = total_cost / mana + (total_cost % mana > 0 ? 1 : 0);
        available = ench.getCount() * mana;
        if (ench.getCount() < consume) {
            return Error.NOT_ENOUGH_MANA;
        }
        if (!gold.isEmpty()) {
            if (gold.getItem() != ((ManaStorage) ench.getItem()).container)
                return Error.CLEAR_GOLD;
            if (64 - gold.getCount() < consume)
                return Error.CLEAR_GOLD;
        }
        return Error.PASS;
    }

    @Override
    public boolean clickMenuButton(PlayerEntity pl, int btn) {
        if (err == Error.PASS) {

            slotsChanged(slot);
        }
        return super.clickMenuButton(pl, btn);
    }

    public enum Error {
        NO_ITEM, NO_SPELL, NOT_UNLOCKED, WRONG_SCROLL, NOT_ENOUGH_MANA, CLEAR_GOLD, PASS;
    }

}
