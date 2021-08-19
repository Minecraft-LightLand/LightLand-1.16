package com.hikarishima.lightland.magic.gui.container.experimental;

import com.google.common.collect.Maps;
import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.gui.container.AbstractContainer;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.registry.ContainerRegistry;
import com.hikarishima.lightland.registry.item.magic.MagicWand;
import com.hikarishima.lightland.registry.item.magic.ManaStorage;
import com.lcy0x1.core.util.SpriteManager;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicCraftContainer extends AbstractContainer {

    public static final SpriteManager MANAGER = new SpriteManager(LightLand.MODID, "magic_craft");

    protected Error err = Error.NO_ITEM;
    protected Item product;
    protected Map<MagicElement, Integer> map = Maps.newLinkedHashMap();

    private int consume = 0, total_cost = 0, available = 0, ench_count = 0, exceed = 0;
    private boolean changing = false;

    public MagicCraftContainer(int wid, PlayerInventory plInv) {
        super(ContainerRegistry.CT_MAGIC_CRAFT, wid, plInv, 13, MANAGER);
        addSlot("wand_slot", stack -> stack.getItem() instanceof MagicWand);
        addSlot("ench_slot", stack -> stack.getItem() instanceof ManaStorage);
        addSlot("gold_slot", stack -> false);
        addSlot("output_slot", stack -> false);
        addSlot("input_slot", stack -> true);
    }

    private Error check() {
        product = null;
        consume = 0;
        total_cost = 0;
        available = 0;
        ench_count = 0;
        exceed = 0;
        map.clear();
        ItemStack wand = slot.getItem(0);
        ItemStack ench = slot.getItem(1);
        ItemStack gold = slot.getItem(2);
        if (wand.isEmpty()) {
            return Error.NO_ITEM;
        }
        if (!(wand.getItem() instanceof MagicWand)) {
            return Error.NO_SPELL;
        }
        MagicWand magicWand = (MagicWand) wand.getItem();
        MagicProduct<?, ?> prod = magicWand.getData(plInv.player, wand);
        if (prod == null || prod.type != MagicRegistry.MPT_CRAFT) {
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
        product = (Item) prod.item;
        total_cost = prod.getCost() * ManaStorage.ARCANE_COST;
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

            //TODO consume input and produce output

            ItemStack ench = slot.getItem(1);
            ench.shrink(consume);
            ItemStack gold = slot.getItem(2);
            if (!gold.isEmpty())
                gold.grow(consume);
            else slot.setItem(2, new ItemStack(((ManaStorage) ench.getItem()).container, consume));
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

    @Override
    public void slotsChanged(IInventory inv) {
        if (changing)
            return;
        err = check();
        super.slotsChanged(inv);
    }

    public enum Error {
        NO_ITEM, NO_SPELL, NOT_UNLOCKED, NOT_ENOUGH_MANA, CLEAR_GOLD, ELEM, PASS;

        public ITextComponent getDesc(MagicCraftContainer cont) {
            String id = "screen.arcane_inject.error." + name().toLowerCase();
            if (this == NOT_ENOUGH_MANA)
                return Translator.get(id, cont.consume - cont.ench_count, cont.total_cost, cont.available);
            if (this == PASS)
                return Translator.get(id, cont.exceed);
            return Translator.get(id);
        }
    }

}
