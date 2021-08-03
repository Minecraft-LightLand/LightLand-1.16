package com.hikarishima.lightland.magic.gui.ability;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.ToServerMsg;
import com.hikarishima.lightland.magic.gui.AbstractHexGui;
import com.hikarishima.lightland.magic.profession.Profession;
import com.hikarishima.lightland.proxy.Proxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ProfessionScreen extends AbstractAbilityScreen {

    public static final ITextComponent TITLE = Translator.get("screen.ability.profession.title");
    private static final int H_DIS = 50, Y_DIS = 50;

    public static boolean canAccess() {
        return MagicHandler.get(Proxy.getPlayer()).abilityPoints.getProfession() == null;
    }

    public ProfessionScreen() {
        super(AbilityTab.PROFESSION, TITLE);
    }

    @Override
    protected void renderInside(MatrixStack matrix, int w, int h, int mx, int my, float partial) {
        fill(matrix, 0, 0, w, h, 0xFF606060);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(w / 2f, h / 2f, 0);
        mx -= w / 2;
        my -= h / 2;
        MagicHandler handler = MagicHandler.get(Proxy.getPlayer());
        for (ProfType e : ProfType.values()) {
            e.render(handler, matrix, mx, my);
        }
        RenderSystem.popMatrix();
    }

    @Override
    public boolean innerMouseClick(int w, int h, double mx, double my) {
        MagicHandler handler = MagicHandler.get(Proxy.getPlayer());
        if (!canAccess())
            return false;
        for (ProfType e : ProfType.values()) {
            if (e.within(mx - w / 2f, my - h / 2f)) {
                if (handler.abilityPoints.setProfession(e.prof)) {
                    ToServerMsg.setProfession(e.prof);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void renderInnerTooltip(MatrixStack matrix, int w, int h, int mx, int my) {
        for (ProfType e : ProfType.values()) {
            if (e.within(mx - w / 2f, my - h / 2f)) {
                renderTooltip(matrix, e.prof.getDesc(), mx, my);
            }
        }
    }

    public enum ProfType {
        MAGICIAN(-H_DIS, -Y_DIS, MagicRegistry.PROF_MAGIC),
        SPELL_CASTER(-H_DIS, 0, MagicRegistry.PROF_SPELL),
        ARCANE(-H_DIS, Y_DIS, MagicRegistry.PROF_ARCANE);

        public final int x, y;
        public final Profession prof;

        ProfType(int x, int y, Profession prof) {
            this.x = x;
            this.y = y;
            this.prof = prof;
        }

        public void render(MagicHandler handler, MatrixStack matrix, int mx, int my) {
            Minecraft.getInstance().getTextureManager().bind(prof.getIcon());
            AbstractHexGui.drawScaled(matrix, x, y, 2);
            if (within(mx, my) && handler.abilityPoints.profession == null)
                fill(matrix, x - 8, y - 8, x + 8, y + 8, 0x80FFFFFF);
        }

        public boolean within(double mx, double my) {
            return mx > x - 8 && mx < x + 8 && my > y - 8 && my < y + 8;
        }

    }

}
