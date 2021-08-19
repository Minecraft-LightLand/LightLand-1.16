package com.hikarishima.lightland.quest.gui;

import com.hikarishima.lightland.quest.player.QuestHandler;
import com.hikarishima.lightland.quest.token.QuestToken;
import com.hikarishima.lightland.proxy.Proxy;
import com.lcy0x1.base.TextScreenUtil;
import com.lcy0x1.base.WindowBox;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;

@ParametersAreNonnullByDefault
@SuppressWarnings("deprecation")
public class QuestScreen extends Screen {

    private static final int SIDE = 100, MAIN = 200, MARGIN = 8, HEIGHT = 200;
    private static final ITextComponent TITLE = new TranslationTextComponent("gui.advancements");

    private final QuestHandler handler;
    private final WindowBox side_window = new WindowBox();
    private final WindowBox main_window = new WindowBox();

    private TokenBox[] tokens;
    private TokenBox selected;

    public QuestScreen() {
        super(TITLE);
        handler = QuestHandler.get(Proxy.getClientPlayer());
    }

    @Override
    protected void init() {
        Collection<QuestToken> list = handler.getTokens(QuestToken.class);
        tokens = list.stream().map(TokenBox::new).toArray(TokenBox[]::new);
        for (TokenBox box : tokens) {
            box.init(this, SIDE - MARGIN * 2, MAIN - MARGIN * 2);
        }
        int x0 = (width - (SIDE + MAIN)) / 2;
        int y0 = (height - HEIGHT) / 2;
        side_window.setSize(this, x0, y0, SIDE, HEIGHT, MARGIN);
        main_window.setSize(this, x0 + SIDE, y0, MAIN, HEIGHT, MARGIN);
    }

    @Override
    public void render(MatrixStack matrix, int mx, int my, float partial) {
        super.renderBackground(matrix);
        int col_0 = 0xFFFFFFFF;
        int col_1 = 0xFF808080;
        int col_2 = 0xFFB0B0B0;
        side_window.render(matrix, 8, col_0, WindowBox.RenderType.MARGIN);
        side_window.render(matrix, 2, col_1, WindowBox.RenderType.MARGIN);
        side_window.startClip(matrix);
        side_window.render(matrix, 0, col_2, WindowBox.RenderType.FILL);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(side_window.x, side_window.y, 0);
        int bmx = mx - side_window.x;
        int bmy = my - side_window.y;
        for (TokenBox box : tokens) {
            Select sel = selected == box ? Select.SELECT :
                    box.box.isMouseIn(bmx, bmy, TokenBox.SIDE_MARGIN) ? Select.HOVER : Select.NONE;
            box.renderBrief(matrix, sel);
            RenderSystem.translatef(0, box.getBriefHeight(), 0);
            bmy -= box.getBriefHeight();
        }
        RenderSystem.popMatrix();
        side_window.endClip(matrix);
        main_window.render(matrix, 8, col_0, WindowBox.RenderType.MARGIN);
        main_window.render(matrix, 2, col_1, WindowBox.RenderType.MARGIN);
        main_window.startClip(matrix);
        main_window.render(matrix, 0, col_2, WindowBox.RenderType.FILL);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(main_window.x, main_window.y, 0);
        if (selected != null) {
            selected.renderMain(matrix);
        }
        RenderSystem.popMatrix();
        main_window.endClip(matrix);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && side_window.isMouseIn(mx, my, 0)) {
            double bmx = mx - side_window.x;
            double bmy = my - side_window.y;
            for (TokenBox box : tokens) {
                if (box.box.isMouseIn(bmx, bmy, TokenBox.SIDE_MARGIN)) {
                    selected = box;
                }
                bmy -= box.getBriefHeight();
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    public static class TokenBox {

        public static final int SIDE_MARGIN = 2;

        public final QuestToken token;
        public final WindowBox box = new WindowBox();

        public IReorderingProcessor title;
        public List<IReorderingProcessor> brief;
        public List<IReorderingProcessor> text;

        public TokenBox(QuestToken token) {
            this.token = token;
        }

        public void init(QuestScreen screen, int side_width, int main_width) {
            title = LanguageMap.getInstance().getVisualOrder(token.getTitle());
            brief = LanguageMap.getInstance().getVisualOrder(TextScreenUtil.splitLines(token.getTitle(), side_width));
            text = LanguageMap.getInstance().getVisualOrder(TextScreenUtil.splitLines(token.getQuestProgressText(), main_width));
            box.setSize(screen, 0, 0, side_width, getBriefHeight(), SIDE_MARGIN);
        }

        public int getBriefHeight() {
            return Math.max(2, brief.size()) * 9 + SIDE_MARGIN * 2;
        }

        public void renderBrief(MatrixStack matrix, Select select) {
            int col = select == Select.NONE ? 0xFFFFFFFF : select == Select.HOVER ? 0xFFFFFF00 : 0xFFB0B0B0;
            box.render(matrix, 2, col, WindowBox.RenderType.MARGIN);
            box.render(matrix, 1, 0xFF808080, WindowBox.RenderType.MARGIN);
            int x = box.x;
            int y = box.y;
            for (IReorderingProcessor t : brief) {
                Minecraft.getInstance().font.draw(matrix, t, x, y, 0xFF000000);
                y += 9;
            }
        }

        public void renderMain(MatrixStack matrix) {
            FontRenderer fr = Minecraft.getInstance().font;
            fr.draw(matrix, title, 0, 0, 0xFF000000);
            int y = 12;
            for (IReorderingProcessor t : text) {
                fr.draw(matrix, t, 0, y, 0xFF000000);
                y += 9;
            }
        }

    }

    public enum Select {
        NONE, HOVER, SELECT
    }

}
