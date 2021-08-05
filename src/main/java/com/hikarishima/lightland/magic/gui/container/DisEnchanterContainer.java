package com.hikarishima.lightland.magic.gui.container;

import com.google.common.collect.Maps;
import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.MagicHolder;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.hikarishima.lightland.registry.ContainerRegistry;
import com.hikarishima.lightland.registry.ItemRegistry;
import com.lcy0x1.base.PredSlot;
import com.lcy0x1.core.util.SpriteManager;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DisEnchanterContainer extends Container {

    public static final SpriteManager MANAGER = new SpriteManager(LightLand.MODID, "disenchanter");

    protected final PlayerInventory plInv;
    protected final IInventory slot = new Inventory(3) {
        @Override
        public void setChanged() {
            super.setChanged();
            slotsChanged(this);
        }
    };

    protected final Map<MagicElement, Integer> map = Maps.newLinkedHashMap();
    protected final Map<Enchantment, IMagicRecipe<?>> ench_map;

    public DisEnchanterContainer(int wid, PlayerInventory plInv) {
        super(ContainerRegistry.CT_DISENCH, wid);
        this.plInv = plInv;
        MANAGER.getSlot("main_slot", (x, y) -> new PredSlot(slot, 0, x, y,
                stack -> stack.isEnchanted() || stack.getItem() == Items.ENCHANTED_BOOK), this::addSlot);
        MANAGER.getSlot("gold_slot", (x, y) -> new PredSlot(slot, 1, x, y,
                stack -> stack.getItem() == Items.GOLD_NUGGET), this::addSlot);
        MANAGER.getSlot("ench_slot", (x, y) -> new PredSlot(slot, 2, x, y,
                stack -> false), this::addSlot);

        int x = MANAGER.getPlInvX();
        int y = MANAGER.getPlInvY();
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlot(new Slot(plInv, j + i * 9 + 9, x + j * 18, y + i * 18));
        for (int k = 0; k < 9; ++k)
            this.addSlot(new Slot(plInv, k, x + k * 18, y + 58));

        ench_map = IMagicRecipe.getMap(plInv.player.level, MagicRegistry.MPT_ENCH);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return player.isAlive();
    }

    @Override
    public boolean clickMenuButton(PlayerEntity pl, int btn) {
        ItemStack stack = slot.getItem(0);
        if (stack.isEnchanted() || stack.getItem() == Items.ENCHANTED_BOOK) {
            Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(stack);
            int[] arr = new int[1];
            enchs.entrySet().removeIf((e) -> {
                if (e.getValue() > 0 && ench_map.containsKey(e.getKey())) {
                    arr[0] += e.getValue();
                    return true;
                }
                return false;
            });
            MagicHolder h = MagicHandler.get(plInv.player).magicHolder;
            map.forEach(h::addElement);
            if (stack.getItem() == Items.ENCHANTED_BOOK) {
                slot.setItem(0, Items.BOOK.getDefaultInstance());
            } else {
                EnchantmentHelper.setEnchantments(enchs, stack);
            }
            ItemStack res = slot.getItem(2);
            if (arr[0] > 64)
                arr[0] = 64;
            if (!res.isEmpty() && 64 - res.getCount() < arr[0])
                arr[0] = 64 - res.getCount();
            ItemStack gold = slot.getItem(1);
            if (gold.getCount() <= arr[0]) {
                arr[0] = gold.getCount();
                slot.setItem(1, ItemStack.EMPTY);
            } else gold.shrink(arr[0]);
            if (res.isEmpty()) {
                slot.setItem(2, new ItemStack(ItemRegistry.ENCHANT_GOLD_NUGGET, arr[0]));
            } else res.grow(arr[0]);
            broadcastChanges();
            slotsChanged(slot);
            return true;
        }
        return super.clickMenuButton(pl, btn);
    }

    @Override
    public void slotsChanged(IInventory inv) {
        ItemStack stack = inv.getItem(0);
        map.clear();
        if (stack.isEnchanted() || stack.getItem() == Items.ENCHANTED_BOOK) {
            for (Map.Entry<Enchantment, Integer> e : EnchantmentHelper.getEnchantments(stack).entrySet()) {
                if (e.getValue() > 0) {
                    Enchantment ench = e.getKey();
                    if (ench_map.containsKey(ench)) {
                        IMagicRecipe<?> r = ench_map.get(ench);
                        for (MagicElement elem : r.getElements()) {
                            if (map.containsKey(elem))
                                map.put(elem, map.get(elem) + e.getValue());
                            else
                                map.put(elem, e.getValue());
                        }
                    }
                }
            }
        }
        super.slotsChanged(inv);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity pl, int id) {
        ItemStack stack = slots.get(id).getItem();
        if (id < 3) {
            if (moveItemStackTo(stack, 3, 39, true)) {
                slotsChanged(slot);
                return stack;
            }
        } else {
            if (slots.get(0).mayPlace(stack)) {
                if (moveItemStackTo(stack, 0, 1, true)) {
                    return stack;
                }
            }
            if (slots.get(1).mayPlace(stack)) {
                if (moveItemStackTo(stack, 1, 2, true)) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
