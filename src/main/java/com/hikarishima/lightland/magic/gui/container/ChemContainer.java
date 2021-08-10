package com.hikarishima.lightland.magic.gui.container;

import com.google.common.collect.Maps;
import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.chem.ChemEffect;
import com.hikarishima.lightland.magic.chem.HashEquationPool;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.registry.ContainerRegistry;
import com.lcy0x1.core.chem.AbChemObj;
import com.lcy0x1.core.util.SpriteManager;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChemContainer extends AbstractContainer implements PacketHandler.SerialMsgCont<ChemPacket> {

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
        addSlot("output_in_slot", stack -> stack.getItem() == Items.GLASS_BOTTLE);
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
        if (btn == ADD && total_item < MAX_ITEM) {
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
        if (btn == CLEAR) {
            elems.clear();
            items.clear();
            total_element = 0;
            total_item = 0;
            return true;
        }
        if (btn == OUT) {
            ItemStack stack = slot.getItem(2);
            ItemStack out = slot.getItem(3);
            if (!out.isEmpty())
                return false;
            if (!stack.isEmpty()) {
                ItemStack potion = Items.POTION.getDefaultInstance();
                total_element = 0;
                total_item = 0;
                elems.clear();
                items.clear();
                stack.shrink(1);
                slot.setItem(3, potion);
                return true;
            }
        }
        return super.clickMenuButton(pl, btn);
    }

    @Override
    public void handle(ChemPacket msg) {
        if (msg.result != null) {
            HashEquationPool pool = HashEquationPool.getPool(plInv.player.level);
            List<EffectInstance> list = new ArrayList<>();
            double redstone = msg.result.map.get("item.redstone");
            double glowstone = msg.result.map.get("item.glowstone_dust");
            msg.result.map.forEach((k, v) -> {
                AbChemObj obj = pool.objects.get(k);
                if (obj instanceof ChemEffect) {
                    ChemEffect ce = (ChemEffect) obj;
                    Effect eff = ce.get();
                    int dur = (int) Math.round(ce.duration * v);
                    int lv = ce.lv;
                    if (ce.duration == 0) {
                        if (v < 1)
                            return;
                        lv = (int) Math.floor(Math.log(v) / Math.log(2) + 1e-3);
                    }
                    if (glowstone >= 1)
                        lv+=ce.boost;
                    if (redstone >= 1)
                        dur *= 2;
                    list.add(new EffectInstance(eff, dur, lv));
                }
            });
            ItemStack stack = slot.getItem(3);
            if (!stack.isEmpty())
                PotionUtils.setCustomEffects(stack, list);
        }
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
