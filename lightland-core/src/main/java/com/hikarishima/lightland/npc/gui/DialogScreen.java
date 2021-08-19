package com.hikarishima.lightland.npc.gui;

import com.hikarishima.lightland.npc.dialog.DialogHolder;
import com.hikarishima.lightland.npc.option.Option;
import com.lcy0x1.base.TextScreenUtil;
import com.lcy0x1.base.WindowBox;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class DialogScreen extends Screen {

    private static final ITextComponent TITLE = new TranslationTextComponent("gui.advancements");
    private static final int MARGIN = 6, MAIN_TEXT_MARGIN = 20, LINE_HEIGHT = 9;

    private final DialogHolder holder;
    private final WindowBox main_box = new WindowBox();

    private List<IReorderingProcessor> text;
    private int text_width, text_height;
    private OptionBox[] options;

    public DialogScreen(DialogHolder holder) {
        super(TITLE);
        this.holder = holder;
    }

    @Override
    public void init() {
        text_width = width - (MARGIN + MAIN_TEXT_MARGIN) * 2;
        text_height = (int) (height * 0.2);
        updateText();
    }

    private void updateText() {
        text = LanguageMap.getInstance().getVisualOrder(TextScreenUtil.splitLines(
                TextComponentUtils.mergeStyles(holder.dialog.getText(), Style.EMPTY).copy(), text_width));
        IReorderingProcessor[] opttext = LanguageMap.getInstance().getVisualOrder(holder.dialog.getOptionText()).toArray(new IReorderingProcessor[0]);
        int box_height = text_height + 2 * MARGIN;
        int option_height = LINE_HEIGHT + 2 * MARGIN;
        main_box.setSize(this, 0, height - box_height - option_height, width, box_height, MARGIN);
        List<OptionBox> list = new ArrayList<>();
        for (int i = 0; i < opttext.length; i++) {
            OptionBox box = new OptionBox();
            box.box = new WindowBox();
            box.option = holder.dialog.next[i];
            box.text = opttext[i];
            box.enabled = box.option.test(Minecraft.getInstance().player);
            list.add(box);
        }
        options = list.toArray(new OptionBox[0]);
        for (int i = 0; i < options.length; i++) {
            int x = width * i / options.length;
            int w = width * (i + 1) / options.length - x;
            options[i].box.setSize(this, x, height - option_height, w, option_height, MARGIN);
        }
    }


    public void render(MatrixStack matrix, int mx, int my, float partial) {
        FontRenderer font = Minecraft.getInstance().font;
        fill(matrix, 0, main_box.y - MARGIN, width, height, 0x80000000);
        for (int i = 0; i < text.size(); i++) {
            font.draw(matrix, text.get(i), MARGIN + MAIN_TEXT_MARGIN, main_box.y + i * LINE_HEIGHT, 0xFFFFFFFF);
        }
        main_box.render(matrix, MARGIN, 0xFFFFFFFF, WindowBox.RenderType.MARGIN);
        main_box.render(matrix, 2, 0xFF606060, WindowBox.RenderType.MARGIN);
        for (OptionBox box : options) {
            int col = box.enabled ? box.box.isMouseIn(mx, my, MARGIN) ? 0xFFFFFF00 : 0xFFFFFFFF : 0xFF808080;
            box.box.render(matrix, MARGIN, col, WindowBox.RenderType.MARGIN);
            box.box.render(matrix, 2, 0xFF606060, WindowBox.RenderType.MARGIN);
        }
        for (OptionBox box : options) {
            IReorderingProcessor option = box.text;
            WindowBox window = box.box;
            float w = Minecraft.getInstance().font.width(option);
            font.draw(matrix, option, window.x + (window.w - w) / 2, window.y, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            for (int i = 0; i < options.length; i++) {
                WindowBox box = options[i].box;
                if (options[i].enabled && box.isMouseIn(mx, my, MARGIN)) {
                    options[i].option.perform(Minecraft.getInstance().player);
                    if (holder.next(i)) {
                        updateText();
                    } else {
                        Minecraft.getInstance().setScreen(null);
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static class OptionBox {

        private IReorderingProcessor text;
        private WindowBox box;
        private Option option;
        private boolean enabled;

    }

}
