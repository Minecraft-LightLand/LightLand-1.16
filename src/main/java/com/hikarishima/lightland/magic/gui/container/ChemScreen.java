package com.hikarishima.lightland.magic.gui.container;

import com.google.common.collect.Maps;
import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.config.Translator;
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
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class ChemScreen extends AbstractScreen<ChemContainer> {

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
            if (process != null && process.complete) {
                if (sm.within("arrow_output", mx, my))
                    output = "arrow_out_2";
                else
                    output = "arrow_out_1";
            } else output = "arrow_out_3";
        }
        if (output != null) {
            sr.draw(matrix, "arrow_output", output);
        }
        int x = sm.getComp("pot").x;
        int y = sm.getComp("pot").y - 18;
        MagicHandler h = MagicHandler.get(Proxy.getClientPlayer());
        RenderSystem.pushMatrix();
        RenderSystem.translated(getGuiLeft(), getGuiTop(), 0);
        for (int i = 0; i < 5; i++) {
            MagicElement e = ChemContainer.ElemType.values()[i].elem;
            boolean can_add = h.magicHolder.getElement(e) > 0 && menu.total_element < ChemContainer.MAX_ELEM;
            int num = menu.elems.getOrDefault(e, 0);
            AbstractHexGui.drawElement(matrix, x + 9 + i * 18, y + 9, e, "" + num, can_add ? 0xFFFFFFFF : AbstractHexGui.RED);
            if (mx > x + i * 18 && mx < x + i * 18 + 18 && my > y && my < y + 18)
                fill(matrix, x + i * 18, y, x + i * 18 + 18, y + 18, 0x80FFFFFF);
        }
        if (display != null) {
            HashEquationPool pool = HashEquationPool.getPool(h.world);
            int i = 0;
            y += 18 + 3;
            x += 9;
            int total = display.map.size();
            for (Map.Entry<String, Double> ent : display.map.entrySet()) {
                ChemObj<?, ?> obj = ChemObj.cast(h, pool.objects.get(ent.getKey()));
                int dx = getObjX(i, total);
                int dy = getObjY(i, total);
                if (obj instanceof ChemElement) {
                    AbstractHexGui.drawElement(matrix, x + dx + 9, y + dy + 9, ((ChemElement) obj).get(), "");
                } else {
                    Item item = null;
                    if (obj instanceof ChemItem) {
                        item = ((ChemItem) obj).get();
                    } else if (obj instanceof ChemEffect) {
                        item = Items.POTION;
                    }
                    if (item == null) {
                        Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(LightLand.MODID, "textures/unknown.png"));
                        AbstractHexGui.drawScaled(matrix, x + dx + 8, y + dy + 8, 1);
                    } else {
                        Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(item.getDefaultInstance(), x + dx, y + dy);
                    }
                }
                i++;
            }
        }
        RenderSystem.popMatrix();
    }

    @Override
    protected void renderTooltip(MatrixStack matrix, int mx, int my) {
        SpriteManager sm = menu.sm;
        MagicHandler h = MagicHandler.get(Proxy.getClientPlayer());
        int x = sm.getComp("pot").x + getGuiLeft();
        int y = sm.getComp("pot").y - 18 + getGuiTop();
        for (int i = 0; i < 5; i++) {
            if (mx > x + i * 18 && mx < x + i * 18 + 18 && my > y && my < y + 18) {
                MagicElement e = ChemContainer.ElemType.values()[i].elem;
                int has = h.magicHolder.getElement(e);
                int cur = menu.total_element;
                int max = ChemContainer.MAX_ELEM;
                int num = menu.elems.getOrDefault(e, 0);
                List<ITextComponent> list = new ArrayList<>();
                list.add(Translator.get(has == 0, "screen.chemistry.has", has));
                list.add(Translator.get(false, "screen.chemistry.in_use", num));
                list.add(Translator.get(cur >= max, "screen.chemistry.current", cur, max));
                renderComponentTooltip(matrix, list, mx, my);
            }
        }
        if (display != null) {
            HashEquationPool pool = HashEquationPool.getPool(h.world);
            int i = 0;
            y += 18 + 3;
            x += 9;
            double redstone = display.map.getOrDefault("item.redstone", 0d);
            double glowstone = display.map.getOrDefault("item.glowstone_dust", 0d);
            int total = display.map.size();
            for (Map.Entry<String, Double> ent : display.map.entrySet()) {
                int dx = getObjX(i, total);
                int dy = getObjY(i, total);
                if (mx > x + dx && mx < x + dx + 18 && my > y + dy && my < y + dy + 18) {
                    ChemObj<?, ?> obj = ChemObj.cast(h, pool.objects.get(ent.getKey()));
                    IFormattableTextComponent text = obj != null ? obj.getDesc() :
                            new StringTextComponent("???").withStyle(TextFormatting.ITALIC);
                    List<ITextComponent> list = new ArrayList<>();
                    list.add(text);
                    double val = ent.getValue();
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
                    break;
                }
                i++;
            }
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
        if (process != null && process.complete && sm.within("arrow_output", mx - getGuiLeft(), my - getGuiTop())) {
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
