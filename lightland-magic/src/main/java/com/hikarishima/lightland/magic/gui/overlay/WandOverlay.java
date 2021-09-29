package com.hikarishima.lightland.magic.gui.overlay;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicProxy;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.ToServerMsg;
import com.hikarishima.lightland.magic.gui.AbstractHexGui;
import com.hikarishima.lightland.magic.gui.ability.ElementalScreen;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.recipe.ritual.AbstractLevelCraftRecipe;
import com.hikarishima.lightland.magic.recipe.ritual.AbstractMagicCraftRecipe;
import com.hikarishima.lightland.magic.recipe.IMagicRecipe;
import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.magic.registry.item.magic.MagicWand;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.hikarishima.lightland.proxy.Proxy;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.*;

public class WandOverlay extends AbstractOverlay {

    public static final WandOverlay INSTANCE = new WandOverlay();
    public static final List<MagicElement> ELEM = new ArrayList<>();
    public static boolean has_magic_wand = false;

    public static void input(int key, int action) {
        if (key == 259 && action == 1 && ELEM.size() > 0) {
            ELEM.remove(ELEM.size() - 1);
        }
        if (key == 257 && action == 1 && ELEM.size() > 0) {
            execute();
            ELEM.clear();
        }
        if (key == 'W' || key == 'A' || key == 'S' || key == 'D' || key == ' ') {
            if (action == 1 && ELEM.size() < 4) {
                MagicElement elem;
                if (key == 'W') elem = MagicRegistry.ELEM_AIR;
                else if (key == 'A') elem = MagicRegistry.ELEM_WATER;
                else if (key == 'S') elem = MagicRegistry.ELEM_EARTH;
                else if (key == 'D') elem = MagicRegistry.ELEM_FIRE;
                else elem = MagicRegistry.ELEM_QUINT;
                ELEM.add(elem);
            }
        }
    }

    protected boolean render() {
        if (!(has_magic_wand = renderMagicWandImpl())) {
            ELEM.clear();
        }
        return has_magic_wand;
    }

    private boolean renderMagicWandImpl() {
        if (player == null || !player.isAlive())
            return false;
        ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
        if (!(stack.getItem() instanceof MagicWand))
            return false;
        MagicWand wand = (MagicWand) stack.getItem();
        if (wand.getData(player, stack) != null)
            return false;
        int x = width / 2 - 27;
        int y = height / 2;
        for (MagicElement elem : ELEM) {
            AbstractHexGui.drawElement(matrix, x, y + 60, elem, "");
            x += 18;
        }
        for (ElementalScreen.ElemType e : ElementalScreen.ElemType.values()) {
            AbstractHexGui.drawElement(matrix, width / 2f + e.x, height / 2f + e.y, e.elem, "");
        }
        MagicProduct<?, ?> p = preview();
        if (p != null) {
            y = height / 2 - 60;
            IFormattableTextComponent text = new TranslationTextComponent(p.getDescriptionID());
            x = (width - font.width(text)) / 2;
            font.draw(matrix, text, x, y, 0xFFFFFFFF);
            if (p.type == MagicRegistry.MPT_ENCH || p.type == MagicRegistry.MPT_EFF) {
                ClientPlayerEntity pl = Proxy.getClientPlayer();
                if (pl != null) {
                    x = (width / 2 + 60);
                    int cost = p.getCost();
                    Optional<AbstractMagicCraftRecipe<?>> opr = Proxy.getWorld().getRecipeManager().getAllRecipesFor(MagicRecipeRegistry.RT_CRAFT).stream()
                            .filter(e -> e instanceof AbstractLevelCraftRecipe).filter(e -> p.recipe.id.equals(e.getMagic()))
                            .findFirst();
                    if (opr.isPresent()) {
                        AbstractMagicCraftRecipe<?> r = opr.get();
                        int lv = r.getLevel(cost);
                        text = Translator.get("tooltip.enchantment_result.result", lv);
                        font.draw(matrix, text, x, y + 10, lv > 0 ? 0xFFFFFF : AbstractHexGui.RED);
                        int next = r.getNextLevel(cost);
                        if (next > 0) {
                            text = Translator.get("tooltip.enchantment_result.next", next);
                            font.draw(matrix, text, x, y + 20, 0xFFFFFFFF);
                        }
                        text = Translator.get("tooltip.enchantment_result.elements");
                        font.draw(matrix, text, x, y + 30, 0xFFFFFFFF);
                        MagicElement[] elems = p.recipe.getElements();
                        Map<MagicElement, Integer> map = new LinkedHashMap<>();
                        for (MagicElement e : elems) {
                            map.put(e, map.getOrDefault(e, 0) + lv);
                        }
                        for (MagicElement e : map.keySet()) {
                            int has = MagicProxy.getHandler().magicHolder.getElement(e);
                            int take = map.get(e);
                            AbstractHexGui.drawElement(matrix, x + 9, y + 50, e, "" + take, has >= take ? 0xFFFFFF : AbstractHexGui.RED);
                            x += 18;
                        }
                    }
                }
            }
            if (p.type == MagicRegistry.MPT_SPELL) {
                Spell<?, ?> spell = (Spell<?, ?>) p.item;
                ClientPlayerEntity pl = Proxy.getClientPlayer();
                if (pl != null) {
                    int mana = spell.getConfig(pl.level, pl).mana_cost;
                    text = Translator.get("tooltip.mana_cost", mana);
                    x = (width - font.width(text)) / 2;
                    font.draw(matrix, text, x, y + 10, 0xFFFFFFFF);
                }
            }
        }
        return true;
    }

    private static MagicProduct<?, ?> preview() {
        ClientPlayerEntity player = Proxy.getClientPlayer();
        if (player == null || !player.isAlive())
            return null;
        MagicHandler handler = MagicHandler.get(player);
        IMagicRecipe<?> r = handler.magicHolder.getTree(ELEM);
        MagicProduct<?, ?> p = handler.magicHolder.getProduct(r);
        if (p != null && p.usable())
            return p;
        return null;
    }

    private static void execute() {
        MagicProduct<?, ?> p = preview();
        if (p != null)
            ToServerMsg.activateWand(p.recipe);
    }

}
