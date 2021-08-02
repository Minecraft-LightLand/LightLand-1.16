package com.hikarishima.lightland.magic.gui.block;

import com.google.common.collect.Maps;
import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.MagicHolder;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.hikarishima.lightland.registry.ContainerRegistry;
import com.lcy0x1.core.util.SpriteManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public class DisEnchanterContainer extends Container {

    public static final SpriteManager MANAGER = new SpriteManager(LightLand.MODID, "disenchanter");

    protected final PlayerInventory plInv;
    protected final IInventory slot = new Inventory(1) {
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
        MANAGER.getSlot("main_slot", (x, y) -> new Slot(slot, 0, x, y) {
            public boolean mayPlace(ItemStack stack) {
                return stack.isEnchanted();
            }

            public int getMaxStackSize() {
                return 1;
            }
        }, this::addSlot);

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
        if (stack.isEnchanted()) {
            Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(stack);
            enchs.entrySet().removeIf((e) -> e.getValue() > 0 && ench_map.containsKey(e.getKey()));
            EnchantmentHelper.setEnchantments(enchs, stack);
            MagicHolder h = MagicHandler.get(plInv.player).magicHolder;
            map.forEach(h::addElement);
            slotsChanged(slot);
            return true;
        }
        return super.clickMenuButton(pl, btn);
    }

    @Override
    public void slotsChanged(IInventory inv) {
        ItemStack stack = inv.getItem(0);
        map.clear();
        if (stack.isEnchanted()) {
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
