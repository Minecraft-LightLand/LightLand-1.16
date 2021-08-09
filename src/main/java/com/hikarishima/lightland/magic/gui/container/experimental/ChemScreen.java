package com.hikarishima.lightland.magic.gui.container.experimental;

import com.google.common.collect.Maps;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.chem.HashEquationPool;
import com.hikarishima.lightland.magic.gui.AbstractHexGui;
import com.hikarishima.lightland.magic.gui.container.AbstractScreen;
import com.hikarishima.lightland.proxy.Proxy;
import com.lcy0x1.core.chem.ReactionPool;
import com.lcy0x1.core.util.SpriteManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class ChemScreen extends AbstractScreen<ChemContainer> {

    protected ReactionPool.Evaluator process = null;
    protected ReactionPool.Result result = null;
    protected ReactionPool.Result display = null;

    private int tick = 0;

    public ChemScreen(ChemContainer cont, PlayerInventory plInv, ITextComponent title) {
        super(cont, plInv, title);
    }

    @Override
    protected void renderBg(MatrixStack matrix, float partial, int mx, int my) {
        mx -= getGuiLeft();
        my -= getGuiTop();
        SpriteManager sm = menu.sm;
        SpriteManager.ScreenRenderer sr = sm.getRenderer(this);
        sr.start(matrix);
        String input = null;
        if (!menu.slot.getItem(0).isEmpty()) {
            if (menu.total_item < ChemContainer.MAX_ITEM) {
                if (sm.within("arrow_input", mx, my))
                    input = "arrow_left_2";
                else
                    input = "arrow_left_1";
            } else input = "arrow_left_3";
        }
        if (input != null) {
            sr.draw(matrix, "arrow_input", input);
        }
        int x = sm.getComp("pot").x;
        int y = sm.getComp("pot").y - 18;
        MagicHandler h = MagicHandler.get(Proxy.getClientPlayer());
        for (int i = 0; i < 5; i++) {
            MagicElement e = ChemContainer.ElemType.values()[i].elem;
            boolean can_add = h.magicHolder.getElement(e) > 0 && menu.total_element < ChemContainer.MAX_ELEM;
            int num = menu.elems.getOrDefault(e, 0);
            AbstractHexGui.drawElement(matrix, x + 9 + i * 18, y + 9, e, "" + num, can_add ? 0xFFFFFFFF : AbstractHexGui.RED);
            if (mx > x + i * 18 && mx < x + i * 18 + 18 && my > y && my < y + 18)
                fill(matrix, x + i * 18, y, x + i * 18 + 18, y + 18, 0x80FFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        SpriteManager sm = menu.sm;
        if (sm.within("arrow_input", mx - getGuiLeft(), my - getGuiTop())) {
            if (click(ChemContainer.ADD)) {
                process(false);
            }
            return true;
        }
        if (sm.within("arrow_output", mx - getGuiLeft(), my - getGuiTop())) {
            if (click(ChemContainer.OUT)) {
                process(true);
            }
            return true;
        }
        int x = getGuiLeft() + sm.getComp("pot").x;
        int y = getGuiTop() + sm.getComp("pot").y - 18;
        for (int i = 0; i < 5; i++) {
            if (mx > x + i * 18 && mx < x + i * 18 + 18 && my > y && my < y + 18) {
                if (click(i)) {
                    process(false);
                }
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean keyPressed(int key, int scan, int modifier) {
        if (menu.plInv.player.isCreative()) {
            if (key == 'W' || key == 'A' || key == 'S' || key == 'D' || key == ' ') {
                int btn;
                if (key == 'W') btn = 2;
                else if (key == 'A') btn = 1;
                else if (key == 'S') btn = 0;
                else if (key == 'D') btn = 3;
                else btn = 4;
                if (click(btn)) {
                    process(false);
                }
                return true;
            }
            if (key == 'E') {
                if (click(ChemContainer.ADD)) {
                    process(false);
                }
                return true;
            }
            if (key == 'C') {
                if (click(ChemContainer.CLEAR)) {
                    process(true);
                }
                return true;
            }
        }
        return super.keyPressed(key, scan, modifier);
    }

    private void process(boolean clear) {
        if (clear) {
            process = null;
            result = null;
            display = null;
            return;
        }
        HashEquationPool hash = HashEquationPool.getPool(menu.plInv.player.level);
        Map<String, Double> map = Maps.newLinkedHashMap();
        if (result != null) {
            result.map.forEach(map::put);
            map.put(menu.temp, map.getOrDefault(menu.temp, 0d) + 1);
            menu.temp = null;
        } else {
            menu.elems.forEach((k, v) -> map.put(hash.cache.get(k.getID()), (double) v));
            menu.items.forEach((k, v) -> map.put(hash.cache.get(Objects.requireNonNull(k.getRegistryName()).toString()), (double) v));
        }
        process = null;
        result = null;
        ReactionPool react = hash.getPool(map);
        process = react.new Evaluator();
        innerTick();
    }

    @Override
    public void tick() {
        super.tick();
        innerTick();
    }

    private void innerTick() {
        tick++;
        if (process != null && !process.complete) {
            result = process.complete(1e-2, 1e7);
            if (process.complete)
                tick = 0;
        }
        if (tick % 20 == 0) {
            tick = 0;
            if (result != null)
                display = result;
        }
    }
}
