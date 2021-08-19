package com.hikarishima.lightland.magic.gui.container;

import com.google.common.collect.Maps;
import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.MagicProxy;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.chem.*;
import com.hikarishima.lightland.magic.gui.AbstractHexGui;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.proxy.Proxy;
import com.lcy0x1.core.chem.ReactionPool;
import com.lcy0x1.core.util.SpriteManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class ChemScreen extends AbstractScreen<ChemContainer> implements ExtraInfo.DoubleInfo<Pair<MagicElement, Integer>, Pair<ChemObj<?, ?>, Double>> {

    private static int rowSize(int total) {
        int n = 4;
        int m = 3;
        while (true) {
            if (total <= n * m)
                return n;
            n++;
            m++;
        }
    }

    private static int getObjX(int i, int total) {
        int n = rowSize(total);
        int m = n - 1;
        if (i / n < m - 1 || n > 4)
            return i % n * 18 * 3 / (n - 1);
        return i % 4 * 18 + (12 - total) % 4 * 9;

    }

    private static int getObjY(int i, int total) {
        int n = rowSize(total);
        int m = n - 1;
        if (m < 3)
            return i / 4 * 18 + (2 - (total - 1) / 4) * 9;
        return i / n * 18 * 2 / (m - 1);
    }

    public static void render(MatrixStack matrix, @Nullable ChemObj<?, ?> obj, int x, int y) {
        if (obj instanceof ChemElement) {
            AbstractHexGui.drawElement(matrix, x + 8, y + 8, ((ChemElement) obj).get(), "");
        } else {
            Item item = null;
            if (obj instanceof ChemItem) {
                item = ((ChemItem) obj).get();
            } else if (obj instanceof ChemEffect) {
                item = Items.POTION;
            }
            if (item == null) {
                Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(LightLandMagic.MODID, "textures/unknown.png"));
                AbstractHexGui.drawScaled(matrix, x + 8, y + 8, 1);
            } else {
                Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(item.getDefaultInstance(), x, y);
            }
        }
    }

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
        String input = null, output = null;
        if (!menu.slot.getItem(0).isEmpty()) {
            if (menu.total_item < ChemContainer.MAX_ITEM) {
                if (sm.within("arrow_input", mx, my))
                    input = "arrow_in_2";
                else
                    input = "arrow_in_1";
            } else input = "arrow_in_3";
        }
        if (input != null) {
            sr.draw(matrix, "arrow_input", input);
        }
        if (!menu.slot.getItem(2).isEmpty()) {
            if (process != null && process.isComplete()) {
                if (sm.within("arrow_output", mx, my))
                    output = "arrow_out_2";
                else
                    output = "arrow_out_1";
            } else output = "arrow_out_3";
        }
        if (output != null) {
            sr.draw(matrix, "arrow_output", output);
        }

        MagicHandler h = MagicProxy.getHandler();
        RenderSystem.pushMatrix();
        RenderSystem.translated(getGuiLeft(), getGuiTop(), 0);
        int _mx = mx;
        int _my = my;
        getInfoA((x, y, _w, _h, e) -> {
            boolean can_add = h.magicHolder.getElement(e.getFirst()) > 0 && menu.total_element < ChemContainer.MAX_ELEM;
            AbstractHexGui.drawElement(matrix, x + 9, y + 9, e.getFirst(), "" + e.getSecond(), can_add ? 0xFFFFFFFF : AbstractHexGui.RED);
            if (_mx > x && _mx < x + _w && _my > y && _my < y + _h)
                fill(matrix, x, y, x + _w, y + _h, 0x80FFFFFF);
        });
        getInfoB((x, y, _w, _h, e) -> {
            render(matrix, e.getFirst(), x, y);
        });
        RenderSystem.popMatrix();
    }

    @Override
    protected void renderTooltip(MatrixStack matrix, int mx, int my) {
        SpriteManager sm = menu.sm;
        MagicHandler h = MagicProxy.getHandler();
        getInfoAMouse(mx - getGuiLeft(), my - getGuiTop(), (x, y, _w, _h, e) -> {
            int has = h.magicHolder.getElement(e.getFirst());
            int cur = menu.total_element;
            int max = ChemContainer.MAX_ELEM;
            int num = e.getSecond();
            List<ITextComponent> list = new ArrayList<>();
            list.add(Translator.get(has == 0, "screen.chemistry.has", has));
            list.add(Translator.get(false, "screen.chemistry.in_use", num));
            list.add(Translator.get(cur >= max, "screen.chemistry.current", cur, max));
            renderComponentTooltip(matrix, list, mx, my);
        });

        if (display != null) {
            double redstone = display.getMap().getOrDefault("item.redstone", 0d);
            double glowstone = display.getMap().getOrDefault("item.glowstone_dust", 0d);
            getInfoBMouse(mx - getGuiLeft(), my - getGuiTop(), (x, y, _w, _h, e) -> {
                ChemObj<?, ?> obj = e.getFirst();
                IFormattableTextComponent text = obj != null ? obj.getDesc() :
                        new StringTextComponent("???").withStyle(TextFormatting.ITALIC);
                List<ITextComponent> list = new ArrayList<>();
                list.add(text);
                double val = e.getSecond();
                val = Math.round(val * 100) / 100d;
                list.add(Translator.get("screen.chemistry.value", val));
                if (obj instanceof ChemEffect) {
                    ChemEffect eff = (ChemEffect) obj;
                    int lv = eff.lv;
                    if (eff.duration > 0) {
                        int dur = (int) (eff.duration * val);
                        if (redstone >= 1)
                            dur *= 2;
                        int min = dur / 20 / 60;
                        int sec = dur / 20 % 60;
                        String str = min + (sec < 10 ? ":0" : ":") + sec;
                        list.add(new StringTextComponent(str));
                    } else {
                        lv = (int) Math.floor(Math.log(val) / Math.log(2) + 1e-3);
                    }
                    if (glowstone >= 1)
                        lv += eff.boost;
                    text.append(" " + Translator.getNumber(lv + 1));
                }
                renderComponentTooltip(matrix, list, mx, my);
            });
        }
        super.renderTooltip(matrix, mx, my);
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
        if (process != null && process.isComplete() && sm.within("arrow_output", mx - getGuiLeft(), my - getGuiTop())) {
            if (click(ChemContainer.OUT)) {
                ChemPacket packet = new ChemPacket(menu.containerId, display);
                menu.handle(packet);
                PacketHandler.send(packet);
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
    public void tick() {
        super.tick();
        innerTick();
    }

    private void process(boolean clear) {
        if (clear) {
            process = null;
            result = null;
            display = null;
            return;
        }
        HashEquationPool hash = MagicProxy.getPool();
        Map<String, Double> map = Maps.newLinkedHashMap();
        if (result != null) {
            result.getMap().forEach(map::put);
            map.put(menu.temp, map.getOrDefault(menu.temp, 0d) + 1);
            menu.temp = null;
        } else {
            menu.elems.forEach((k, v) -> map.put(hash.cache.get(k.getID()), (double) v));
            menu.items.forEach((k, v) -> map.put(hash.cache.get(Objects.requireNonNull(k.getRegistryName()).toString()), (double) v));
        }
        process = null;
        result = null;
        ReactionPool react = hash.getPool(map, menu.env);
        process = react.newEvaluator();
        innerTick();
    }

    private void innerTick() {
        tick++;
        if (process != null && !process.isComplete()) {
            result = process.complete(1e-2, 1e7);
            if (process.isComplete())
                tick = 0;
        }
        if (tick % 20 == 0) {
            tick = 0;
            if (result != null)
                display = result;
        }
    }

    @Override
    public void getInfoA(Con<Pair<MagicElement, Integer>> con) {
        int x = menu.sm.getComp("pot").x;
        int y = menu.sm.getComp("pot").y - 18;
        for (int i = 0; i < 5; i++) {
            MagicElement e = ChemContainer.ElemType.values()[i].elem;
            int num = menu.elems.getOrDefault(e, 0);
            if (con.apply(x + i * 18, y, 18, 18, Pair.of(e, num)))
                break;
        }
    }

    @Override
    public void getInfoB(Con<Pair<ChemObj<?, ?>, Double>> con) {
        if (display != null) {
            HashEquationPool pool = MagicProxy.getPool();
            MagicHandler h = MagicProxy.getHandler();
            int x = menu.sm.getComp("pot").x;
            int y = menu.sm.getComp("pot").y - 18;
            int i = 0;
            y += 18 + 3;
            x += 9;
            int total = display.getMap().size();
            for (Map.Entry<String, Double> ent : display.getMap().entrySet()) {
                ChemObj<?, ?> obj = ChemObj.cast(h, pool.getObjects().get(ent.getKey()));
                int dx = getObjX(i, total);
                int dy = getObjY(i, total);
                if (con.apply(x + dx + 1, y + dy + 1, 16, 16, Pair.of(obj, ent.getValue())))
                    break;
                i++;
            }
        }
    }
}
