package com.hikarishima.lightland.magic.gui.ability;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.ToServerMsg;
import com.hikarishima.lightland.magic.gui.AbstractHexGui;
import com.hikarishima.lightland.proxy.Proxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ElementalScreen extends AbstractAbilityScreen {

    public static final ITextComponent TITLE = Translator.get("screen.ability.elemental.title");
    private static final int RADIUS = 30;

    public static boolean canAccess() {
        MagicHandler handler = MagicHandler.get(Proxy.getPlayer());
        return handler.abilityPoints.canLevelElement() ||
                MagicRegistry.ELEMENT.getValues().stream()
                        .anyMatch(e -> handler.magicHolder.getElementalMastery(e) > 0);
    }

    protected ElementalScreen() {
        super(AbilityTab.ELEMENT, TITLE);
    }

    @Override
    protected void renderInside(MatrixStack matrix, int w, int h, int mx, int my, float partial) {
        fill(matrix, 0, 0, w, h, 0xFF606060);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(w / 2f, h / 2f, 0);
        mx -= w / 2;
        my -= h / 2;
        MagicHandler handler = MagicHandler.get(Proxy.getPlayer());
        for (ElemType e : ElemType.values()) {
            e.renderElem(handler, matrix, mx, my);
        }
        RenderSystem.popMatrix();
    }

    @Override
    public boolean innerMouseClick(int w, int h, double mx, double my) {
        MagicHandler handler = MagicHandler.get(Proxy.getPlayer());
        if (!handler.abilityPoints.canLevelElement())
            return false;
        for (ElemType e : ElemType.values()) {
            if (e.within(mx - w / 2f, my - h / 2f)) {
                if (handler.magicHolder.addElementalMastery(e.elem)) {
                    ToServerMsg.addElemMastery(e.elem);
                    handler.abilityPoints.levelElement();
                }
            }
        }
        return false;
    }

    @Override
    public void renderInnerTooltip(MatrixStack matrix, int w, int h, int mx, int my) {
        MagicHandler handler = MagicHandler.get(Proxy.getPlayer());
        for (ElemType e : ElemType.values()) {
            if (e.within(mx - w / 2f, my - h / 2f)) {
                int lv = handler.magicHolder.getElementalMastery(e.elem);
                int count = handler.magicHolder.getElement(e.elem);
                int rem = handler.abilityPoints.element;
                List<ITextProperties> list = new ArrayList<>();
                list.add(e.elem.getDesc());
                list.add(Translator.get("screen.ability.elemental.desc.lv", lv));
                list.add(Translator.get("screen.ability.elemental.desc.count", count));
                list.add(Translator.get("screen.ability.elemental.desc.cost", 1, rem));
                renderTooltip(matrix, LanguageMap.getInstance().getVisualOrder(list), mx, my);
            }
        }
    }


    public enum ElemType {
        E(0, -RADIUS, MagicRegistry.ELEM_EARTH),
        W(-RADIUS, 0, MagicRegistry.ELEM_WATER),
        A(0, RADIUS, MagicRegistry.ELEM_AIR),
        F(RADIUS, 0, MagicRegistry.ELEM_FIRE),
        Q(0, 0, MagicRegistry.ELEM_QUINT);

        public final int x, y;
        public final MagicElement elem;

        ElemType(int x, int y, MagicElement elem) {
            this.x = x;
            this.y = y;
            this.elem = elem;
        }

        public void renderElem(MagicHandler handler, MatrixStack matrix, int mx, int my) {
            int lv = handler.magicHolder.getElementalMastery(elem);
            int count = handler.magicHolder.getElement(elem);
            AbstractHexGui.drawElement(matrix, x, y, elem, "" + count);
            if (within(mx, my) && handler.abilityPoints.canLevelElement())
                fill(matrix, x - 8, y - 8, x + 8, y + 8, 0x80FFFFFF);
        }

        public boolean within(double mx, double my) {
            return mx > x - 8 && mx < x + 8 && my > y - 8 && my < y + 8;
        }

    }

}
