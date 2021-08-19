package com.hikarishima.lightland.magic.gui.container;

import com.google.common.collect.Maps;
import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneItemCraftHelper;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneItemUseHelper;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.registry.ContainerRegistry;
import com.hikarishima.lightland.registry.item.magic.MagicWand;
import com.hikarishima.lightland.registry.item.magic.ManaStorage;
import com.lcy0x1.core.util.SpriteManager;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArcaneInjectContainer extends AbstractContainer {

    public static final SpriteManager MANAGER = new SpriteManager(LightLand.MODID, "arcane_inject");
    protected Error err = Error.NO_ITEM;
    protected Arcane spell = null;
    protected Map<MagicElement, Integer> map = Maps.newLinkedHashMap();

    private int consume = 0, total_cost = 0, available = 0, ench_count = 0, exceed = 0;
    private boolean changing = false;

    public ArcaneInjectContainer(int wid, PlayerInventory plInv) {
        super(ContainerRegistry.CT_ARCANE_INJECT, wid, plInv, 5, MANAGER);
        addSlot("wand_slot", stack -> stack.getItem() instanceof MagicWand);
        addSlot("input_slot", ArcaneItemUseHelper::isArcaneItem);
        addSlot("ench_slot", stack -> stack.getItem() instanceof ManaStorage);
        addSlot("output_slot", stack -> false);
        addSlot("gold_slot", stack -> false);
    }

    @Override
    public void slotsChanged(IInventory inv) {
        if (changing)
            return;
        err = check();
        super.slotsChanged(inv);
    }

    private Error check() {
        spell = null;
        consume = 0;
        total_cost = 0;
        available = 0;
        ench_count = 0;
        exceed = 0;
        map.clear();
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
        if (prod == null || prod.type != MagicRegistry.MPT_ARCANE) {
            return Error.NO_SPELL;
        }
        if (!prod.usable()) {
            return Error.NOT_UNLOCKED;
        }
        for (MagicElement elem : prod.recipe.getElements()) {
            if (map.containsKey(elem))
                map.put(elem, map.get(elem) + 1);
            else map.put(elem, 1);
        }
        MagicHandler handler = MagicHandler.get(plInv.player);
        for (MagicElement elem : map.keySet()) {
            if (map.get(elem) > handler.magicHolder.getElement(elem))
                return Error.ELEM;
        }
        spell = (Arcane) prod.item;
        if (!handler.magicAbility.isArcaneTypeUnlocked(spell.type)) {
            return Error.TYPE_NOT_UNLOCKED;
        }
        total_cost = prod.getCost() * ManaStorage.ARCANE_COST;
        ArcaneType.Weapon weapon = spell.type.weapon;
        if (!weapon.isValid(input)) {
            return Error.WRONG_ITEM;
        }
        if (ArcaneItemCraftHelper.getArcaneOnItem(input, spell.type) != null) {
            return Error.REPEAT;
        }
        if (ench.isEmpty() || !(ench.getItem() instanceof ManaStorage)) {
            return Error.NOT_ENOUGH_MANA;
        }
        int mana = ((ManaStorage) ench.getItem()).mana;
        consume = total_cost / mana + (total_cost % mana > 0 ? 1 : 0);
        ench_count = ench.getCount();
        available = ench.getCount() * mana;
        exceed = consume * mana - total_cost;
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
            changing = true;
            ItemStack input = slot.getItem(1);
            slot.setItem(1, ItemStack.EMPTY);
            ArcaneItemCraftHelper.setArcaneOnItem(input, spell);
            slot.setItem(3, input);
            ItemStack ench = slot.getItem(2);
            ench.shrink(consume);
            ItemStack gold = slot.getItem(4);
            if (!gold.isEmpty())
                gold.grow(consume);
            else slot.setItem(4, new ItemStack(((ManaStorage) ench.getItem()).container, consume));
            MagicHandler handler = MagicHandler.get(plInv.player);
            for (MagicElement elem : map.keySet()) {
                handler.magicHolder.addElement(elem, -map.get(elem));
            }
            changing = false;
            slotsChanged(slot);
            return true;
        }
        return super.clickMenuButton(pl, btn);
    }

    public enum Error {
        NO_ITEM, NO_SPELL, NOT_UNLOCKED, TYPE_NOT_UNLOCKED, WRONG_ITEM, NOT_ENOUGH_MANA, CLEAR_GOLD, ELEM, PASS, REPEAT;

        public ITextComponent getDesc(ArcaneInjectContainer cont) {
            String id = "screen.arcane_inject.error." + name().toLowerCase();
            if (this == WRONG_ITEM)
                return Translator.get(id + "." + cont.spell.type.weapon.name().toLowerCase());
            if (this == NOT_ENOUGH_MANA)
                return Translator.get(id, cont.consume - cont.ench_count, cont.total_cost, cont.available);
            if (this == PASS)
                return Translator.get(id, cont.exceed);
            return Translator.get(id);
        }
    }

}
