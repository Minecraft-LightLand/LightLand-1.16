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
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AbilityScreen extends AbstractAbilityScreen {

    public static final ITextComponent TITLE = Translator.get("screen.ability.ability.title");
    private static final int H_DIS = 50, Y_DIS = 30;

    public AbilityScreen() {
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
                if (e.type.checkLevelUp(handler) == null) {
                    e.type.doLevelUp(handler);
                    ToServerMsg.levelUpAbility(e.type);
                }
            }
        }
        return false;
    }

    @Override
    public void renderInnerTooltip(MatrixStack matrix, int w, int h, int mx, int my) {
        MagicHandler handler = MagicHandler.get(Proxy.getPlayer());
        for (AbilityType e : AbilityType.values()) {
            if (e.within(mx - w / 2f, my - h / 2f)) {
                int lv = e.level.apply(handler);
                int cost = e.cost.apply(handler.abilityPoints);
                List<ITextProperties> list = new ArrayList<>();
                list.add(e.getDesc());
                list.add(Translator.get("screen.ability.ability.desc.lv", lv));
                list.add(Translator.get("screen.ability.ability.desc.cost", 1, cost));
                String lvup = e.type.checkLevelUp(handler);
                if (lvup != null)
                    list.add(Translator.get(lvup).withStyle(TextFormatting.RED));
                renderTooltip(matrix, LanguageMap.getInstance().getVisualOrder(list), mx, my);
                renderTooltip(matrix, e.getDesc(), mx, my);
            }
        }
    }

    public enum AbilityType {
        HEALTH(AbilityPoints.LevelType.HEALTH, e -> e.abilityPoints.health, e -> e.general + e.body, "health", -H_DIS, -Y_DIS),
        STRENGTH(AbilityPoints.LevelType.STRENGTH, e -> e.abilityPoints.strength, e -> e.general + e.body, "strength", -H_DIS, 0),
        SPEED(AbilityPoints.LevelType.SPEED, e -> e.abilityPoints.speed, e -> e.general + e.body, "speed", -H_DIS, Y_DIS),
        MAGIC(AbilityPoints.LevelType.MANA, e -> e.magicAbility.magic_level, e -> e.general + e.magic, "magic", H_DIS, -Y_DIS),
        SPELL(AbilityPoints.LevelType.SPELL, e -> e.magicAbility.spell_level, e -> e.general + e.magic, "spell", H_DIS, 0);

        public final AbilityPoints.LevelType type;
        public final Function<MagicHandler, Integer> level;
        public final Function<AbilityPoints, Integer> cost;
        public final String icon;
        public final int x, y;

        AbilityType(AbilityPoints.LevelType type, Function<MagicHandler, Integer> getter, Function<AbilityPoints, Integer> cost, String icon, int x, int y) {
            this.type = type;
            this.level = getter;
            this.cost = cost;
            this.icon = icon;
            this.x = x;
            this.y = y;
        }

        public void render(MagicHandler handler, MatrixStack matrix, int mx, int my) {
            Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(LightLand.MODID, "textures/ability/" + icon + ".png"));
            AbstractHexGui.drawScaled(matrix, x, y, 2);
            if (within(mx, my) && type.checkLevelUp(MagicHandler.get(Proxy.getPlayer())) == null)
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
