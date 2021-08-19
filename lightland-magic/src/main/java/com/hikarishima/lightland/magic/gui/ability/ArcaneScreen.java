package com.hikarishima.lightland.magic.gui.ability;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.ToServerMsg;
import com.hikarishima.lightland.magic.gui.AbstractHexGui;
import com.hikarishima.lightland.proxy.Proxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArcaneScreen extends AbstractAbilityScreen {

    public static final ITextComponent TITLE = Translator.get("screen.ability.arcane.title");

    public static boolean canAccess() {
        MagicHandler handler = Proxy.getHandler();
        return handler.abilityPoints.canLevelArcane() ||
                MagicRegistry.ARCANE_TYPE.getValues().stream()
                        .anyMatch(handler.magicAbility::isArcaneTypeUnlocked);
    }

    protected ArcaneScreen() {
        super(AbilityTab.ARCANE, TITLE);
    }

    @Override
    protected void renderInside(MatrixStack matrix, int w, int h, int mx, int my, float partial) {
        fill(matrix, 0, 0, w, h, 0xFF606060);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(w / 2f, h / 2f, 0);
        mx -= w / 2;
        my -= h / 2;
        MagicHandler handler = Proxy.getHandler();
        for (ArcaneEntry e : ArcaneEntry.values()) {
            e.render(handler, matrix, mx, my);
        }
        RenderSystem.popMatrix();
    }

    @Override
    public boolean innerMouseClick(int w, int h, double mx, double my) {
        MagicHandler handler = Proxy.getHandler();
        if (!canAccess())
            return false;
        for (ArcaneEntry e : ArcaneEntry.values()) {
            if (e.within(mx - w / 2f, my - h / 2f)) {
                if (handler.abilityPoints.canLevelArcane()) {
                    handler.magicAbility.unlockArcaneType(e.type, false);
                    ToServerMsg.unlockArcaneType(e.type);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void renderInnerTooltip(MatrixStack matrix, int w, int h, int mx, int my) {
        MagicHandler handler = Proxy.getHandler();
        for (ArcaneEntry e : ArcaneEntry.values()) {
            if (e.within(mx - w / 2f, my - h / 2f)) {
                int cost = handler.abilityPoints.arcane + handler.abilityPoints.magic + handler.abilityPoints.general;
                List<ITextProperties> list = new ArrayList<>();
                list.add(e.type.getDesc());
                if (!handler.magicAbility.isArcaneTypeUnlocked(e.type)) {
                    list.add(Translator.get("screen.ability.arcane.cost", 1, cost));
                }
                list.add(Translator.get("screen.ability.arcane.activate." + e.type.hit.name().toLowerCase()));
                renderTooltip(matrix, LanguageMap.getInstance().getVisualOrder(list), mx, my);
            }
        }
    }

    public enum ArcaneEntry {
        ALKAID(ArcaneType.ALKAID, -60, -15),
        MIZAR(ArcaneType.MIZAR, -30, -15),
        ALIOTH(ArcaneType.ALIOTH, 0, -15),
        MEGERZ(ArcaneType.MEGREZ, 30, -15),
        PHECDA(ArcaneType.PHECDA, 30, 15),
        MERAK(ArcaneType.MERAK, 60, 15),
        DUBHE(ArcaneType.DUBHE, 60, -15);

        public final ArcaneType type;
        public final int x, y;


        ArcaneEntry(ArcaneType type, int x, int y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }


        public void render(MagicHandler handler, MatrixStack matrix, int mx, int my) {
            boolean unlocked = handler.magicAbility.isArcaneTypeUnlocked(type);
            boolean hover = within(mx, my) && !unlocked;
            AbstractHexGui.drawFrame(matrix, unlocked ? FrameType.CHALLENGE : FrameType.TASK, hover, x, y);
            ItemStack stack = type.getStack();
            Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(stack, x - 8, y - 8);
        }

        public boolean within(double mx, double my) {
            return mx > x - 8 && mx < x + 8 && my > y - 8 && my < y + 8;
        }


    }

}
