package com.hikarishima.lightland.magic.gui.container;

import com.google.common.collect.Maps;
import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.MagicHolder;
import com.hikarishima.lightland.magic.recipe.IMagicRecipe;
import com.hikarishima.lightland.magic.registry.MagicContainerRegistry;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import com.lcy0x1.core.util.SpriteManager;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DisEnchanterContainer extends AbstractContainer {

    public static final SpriteManager MANAGER = new SpriteManager(LightLandMagic.MODID, "disenchanter");

    protected final Map<MagicElement, Integer> map = Maps.newLinkedHashMap();
    protected final Map<Enchantment, IMagicRecipe<?>> ench_map;

    public DisEnchanterContainer(int wid, PlayerInventory plInv) {
        super(MagicContainerRegistry.CT_DISENCH, wid, plInv, 3, MANAGER);
        addSlot("main_slot", stack -> stack.isEnchanted() || stack.getItem() == Items.ENCHANTED_BOOK);
        addSlot("gold_slot", stack -> stack.getItem() == Items.GOLD_NUGGET);
        addSlot("ench_slot", stack -> false);
        ench_map = IMagicRecipe.getMap(plInv.player.level, MagicRegistry.MPT_ENCH);
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
                slot.setItem(2, new ItemStack(MagicItemRegistry.ENCHANT_GOLD_NUGGET, arr[0]));
            } else res.grow(arr[0]);
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

}
