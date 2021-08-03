package com.hikarishima.lightland.magic.gui.ability;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.capabilities.AbilityPoints;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.ToServerMsg;
import com.hikarishima.lightland.magic.gui.AbstractHexGui;
import com.hikarishima.lightland.proxy.Proxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AbilityScreen extends AbstractAbilityScreen {

    public static final ITextComponent TITLE = Translator.get("screen.ability.ability.title");
    private static final int H_DIS = 50, Y_DIS = 50;

    protected AbilityScreen() {
        super(AbilityTab.ABILITY, TITLE);
    }

    @Override
    protected void renderInside(MatrixStack matrix, int w, int h, int mx, int my, float partial) {
        fill(matrix, 0, 0, w, h, 0xFF606060);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(w / 2f, h / 2f, 0);
        mx -= w / 2;
        my -= h / 2;
        MagicHandler handler = MagicHandler.get(Proxy.getPlayer());
        for (AbilityType e : AbilityType.values()) {
            e.render(handler, matrix, mx, my);
        }
        RenderSystem.popMatrix();

    }

    @Override
    public boolean innerMouseClick(int w, int h, double mx, double my) {
        MagicHandler handler = MagicHandler.get(Proxy.getPlayer());
        if (!handler.abilityPoints.canLevelElement())
            return false;
        for (AbilityType e : AbilityType.values()) {
            if (e.within(mx - w / 2f, my - h / 2f)) {
                if (e.type.check.test(handler)) {
                    e.type.run.accept(handler);
                    ToServerMsg.levelUpAbility(e.type);
                }
            }
        }
        return false;
    }

    @Override
    public void renderInnerTooltip(MatrixStack matrix, int w, int h, int mx, int my) {
        for (AbilityType e : AbilityType.values()) {
            if (e.within(mx - w / 2f, my - h / 2f)) {
                renderTooltip(matrix, e.getDesc(), mx, my);
            }
        }
    }

    public enum AbilityType {
        HEALTH(AbilityPoints.LevelType.HEALTH, "health", -H_DIS, -Y_DIS),
        STRENGTH(AbilityPoints.LevelType.STRENGTH, "strength", -H_DIS, 0),
        SPEED(AbilityPoints.LevelType.SPEED, "speed", -H_DIS, Y_DIS),
        MAGIC(AbilityPoints.LevelType.MANA, "magic", H_DIS, -Y_DIS),
        SPELL(AbilityPoints.LevelType.SPELL, "spell", H_DIS, 0);

        public final AbilityPoints.LevelType type;
        public final String icon;
        public final int x, y;

        AbilityType(AbilityPoints.LevelType type, String icon, int x, int y) {
            this.type = type;
            this.icon = icon;
            this.x = x;
            this.y = y;
        }

        public void render(MagicHandler handler, MatrixStack matrix, int mx, int my) {
            Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(LightLand.MODID, "textures/ability/" + icon + ".png"));
            AbstractHexGui.drawScaled(matrix, x, y, 1);
            if (within(mx, my) && type.check.test(MagicHandler.get(Proxy.getPlayer())))
                fill(matrix, x - 8, y - 8, x + 8, y + 8, 0x80FFFFFF);
        }

        public boolean within(double mx, double my) {
            return mx > x - 8 && mx < x + 8 && my > y - 8 && my < y + 8;
        }

        public ITextComponent getDesc() {
            return Translator.get("screen.ability.ability." + icon);
        }

    }

}
