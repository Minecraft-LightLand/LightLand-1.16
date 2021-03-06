package com.hikarishima.lightland.magic.gui.overlay;

import com.hikarishima.lightland.config.StringSubstitution;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.capabilities.AbilityPoints;
import com.hikarishima.lightland.magic.capabilities.weight.WeightCalculator;
import com.hikarishima.lightland.proxy.Proxy;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.MathHelper;

public class ManaOverlay extends AbstractOverlay {

    public static final ManaOverlay INSTANCE = new ManaOverlay();

    protected boolean render() {
        if (handler == null || handler.abilityPoints.profession == null)
            return false;
        int x0 = 4;
        int dy = 10;
        int y0 = dy;
        float f0 = 1f * handler.abilityPoints.exp / AbilityPoints.expRequirement(handler.abilityPoints.level);
        String s0 = "Lv." + handler.abilityPoints.level;
        renderBar(x0, y0, f0, s0, 0, 0x80FF20);
        y0 += dy;
        if (handler.magicAbility.getMaxMana() > 0) {
            float f1 = 1f * handler.magicAbility.getMana() / handler.magicAbility.getMaxMana();
            String s1 = StringSubstitution.toString(Translator.get("screen.overlay.mana")) + handler.magicAbility.getMana() + "/" + handler.magicAbility.getMaxMana();
            renderBar(x0, y0, f1, s1, 0, 0x80FF20);
        }
        y0 += dy;
        int endur = handler.magicAbility.getMaxSpellEndurance();
        if (endur > 0) {
            float f2 = 1f * handler.magicAbility.getSpellLoad() / endur % 1;
            String s2 = StringSubstitution.toString(Translator.get("screen.overlay.load")) + (handler.magicAbility.getSpellLoad() / endur);
            renderBar(x0, y0, f2, s2, 0, 0x80FF20);
        }
        y0 += dy;
        int base = handler.abilityPoints.getWeightAble();
        int load = WeightCalculator.getTotalWeight(Proxy.getClientPlayer());
        float f3 = MathHelper.clamp(1f * load / base, 0, 2) / 2;
        String s3 = StringSubstitution.toString(Translator.get("screen.overlay.weight")) + load + "/" + base;
        renderBar(x0, y0, f3, s3, 0, 0x80FF20);
        return false;
    }

    private void renderBar(int x0, int y0, float f, String s, int bg, int col) {
        int w = 182;
        int k = (int) (f * (w + 1));
        tm.bind(AbstractGui.GUI_ICONS_LOCATION);
        blit(matrix, x0, y0, 0, 64, w, 5);
        blit(matrix, x0, y0, 0, 69, k, 5);
        int x = x0 + (w - font.width(s)) / 2;
        int y = y0 - 1;
        font.draw(matrix, s, x + 1, y, bg);
        font.draw(matrix, s, x - 1, y, bg);
        font.draw(matrix, s, x, y + 1, bg);
        font.draw(matrix, s, x, y - 1, bg);
        font.draw(matrix, s, x, y, col);
    }

}
