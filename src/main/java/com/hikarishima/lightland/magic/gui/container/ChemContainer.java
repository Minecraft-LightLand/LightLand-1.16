package com.hikarishima.lightland.magic.gui.container;

import com.google.common.collect.Maps;
import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.chem.HashEquationPool;
import com.hikarishima.lightland.magic.gui.container.AbstractContainer;
import com.hikarishima.lightland.registry.ContainerRegistry;
import com.lcy0x1.core.util.SpriteManager;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChemContainer extends AbstractContainer {

    public static final SpriteManager MANAGER = new SpriteManager(LightLand.MODID, "chemistry");
    public static final int ADD = 5, CLEAR = 6, OUT = 7, MAX_ELEM = 8, MAX_ITEM = 8;

    protected Map<MagicElement, Integer> elems = Maps.newLinkedHashMap();
    protected Map<Item, Integer> items = Maps.newLinkedHashMap();
    protected int total_element = 0, total_item = 0;

    protected String temp = null;

    public ChemContainer(int wid, PlayerInventory plInv) {
        super(ContainerRegistry.CT_CHEM, wid, plInv, 4, MANAGER);
        addSlot("input_in_slot", stack -> HashEquationPool.getChemObj(plInv.player.level, stack.getItem()) != null);
        addSlot("input_out_slot", stack -> false);
        addSlot("output_in_slot", stack -> false);
        addSlot("output_out_slot", stack -> false);
    }

    @Override
    public boolean clickMenuButton(PlayerEntity pl, int btn) {
        if (btn < 5 && total_element < MAX_ELEM) {
            ElemType e = ElemType.values()[btn];
            MagicHandler handler = MagicHandler.get(plInv.player);
            if (handler.magicHolder.getElement(e.elem) > 0) {
                elems.put(e.elem, elems.getOrDefault(e.elem, 0) + 1);
                handler.magicHolder.addElement(e.elem, -1);
                total_element++;
                if (pl.level.isClientSide())
                    temp = HashEquationPool.getPool(pl.level).cache.get(e.elem.getID());
                return true;
            }
        }
        if (btn == 5 && total_item < MAX_ITEM) {
            ItemStack stack = slot.getItem(0);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                items.put(stack.getItem(), items.getOrDefault(item, 0) + 1);
                total_item++;
                if (pl.level.isClientSide())
                    temp = HashEquationPool.getPool(pl.level).cache.get(item.getRegistryName().toString());
                stack.shrink(1);
                return true;
            }
        }
        if (btn == 6) {
            elems.clear();
            items.clear();
            total_element = 0;
            total_item = 0;
            return true;
        }
        return super.clickMenuButton(pl, btn);
    }

    @Override
    public void slotsChanged(IInventory inv) {

        super.slotsChanged(inv);
    }

    public enum ElemType {
        E(MagicRegistry.ELEM_EARTH),
        W(MagicRegistry.ELEM_WATER),
        A(MagicRegistry.ELEM_AIR),
        F(MagicRegistry.ELEM_FIRE),
        Q(MagicRegistry.ELEM_QUINT);

        public final MagicElement elem;

        ElemType(MagicElement elem) {
            this.elem = elem;
        }

    }

}
