package com.hikarishima.lightland.magic.gui.ability;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.ToServerMsg;
import com.hikarishima.lightland.magic.gui.hex.AbstractHexGui;
import com.hikarishima.lightland.proxy.Proxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ElementalScreen extends AbstractAbilityScreen {

    public static final ITextComponent TITLE = Translator.get("screen.ability.elemental.title");
    private static final int RADIUS = 50;

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
        //TODO
        int r = 50;
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
    public boolean mouseClicked(double mx, double my, int button) {
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean innerMouseClick(int w, int h, double mx, double my) {
        MagicHandler handler = MagicHandler.get(Proxy.getPlayer());
        if (!handler.abilityPoints.canLevelElement())
            return false;
        for (ElemType e : ElemType.values()) {
            if (e.within(mx - w / 2f, my - h / 2f)) {
                if (handler.magicHolder.addElementalMastery(e.elem))
                    ToServerMsg.addElemMastery(e.elem);
            }
        }
        return false;
    }

    @Override
    public void renderInnerTooltip(MatrixStack matrix, int w, int h, int mx, int my) {
        for (ElemType e : ElemType.values()) {
            if (e.within(mx - w / 2f, my - h / 2f))
                renderTooltip(matrix, e.elem.getDesc(), mx, my);
        }
    }


    public enum ElemType {
        E(0, -RADIUS, MagicRegistry.ELEM_EARTH),
        W(-RADIUS, 0, MagicRegistry.ELEM_WATER),
        A(0, RADIUS, MagicRegistry.ELEM_AIR),
        F(RADIUS, 0, MagicRegistry.ELEM_FIRE),
        Q(0, 0, MagicRegistry.ELEM_VOID);

        public final int x, y;
        public final MagicElement elem;

        ElemType(int x, int y, MagicElement elem) {
            this.x = x;
            this.y = y;
            this.elem = elem;
        }

        public void renderElem(MagicHandler handler, MatrixStack matrix, int mx, int my) {
            AbstractHexGui.drawElement(matrix, x, y, elem, handler.magicHolder.getElementalMastery(elem));
            if (within(mx, my) && handler.abilityPoints.canLevelElement())
                fill(matrix, x - 8, y - 8, x + 8, y + 8, 0x80FFFFFF);
        }

        public boolean within(double mx, double my) {
            return mx > x - 8 && mx < x + 8 && my > y - 8 && my < y + 8;
        }

    }

}
