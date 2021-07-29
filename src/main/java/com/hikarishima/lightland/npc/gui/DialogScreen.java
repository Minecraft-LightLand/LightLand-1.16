package com.hikarishima.lightland.npc.gui;

import com.hikarishima.lightland.npc.dialog.DialogHolder;
import com.lcy0x1.base.WindowBox;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.*;

import java.util.List;

public class DialogScreen extends Screen {

    private static final ITextComponent TITLE = new TranslationTextComponent("gui.advancements");
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
    private static final int MARGIN = 6, MAIN_TEXT_MARGIN = 20, DARK = 12, LINE_HEIGHT = 9;

    private final DialogHolder holder;
    private final WindowBox main_box = new WindowBox();

    private List<IReorderingProcessor> text;
    private IReorderingProcessor[] options;
    private WindowBox[] option_box;
    private int text_width, text_height;

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
        text = LanguageMap.getInstance().getVisualOrder(splitLines(
                TextComponentUtils.mergeStyles(holder.dialog.getText(), Style.EMPTY).copy(), text_width));
        options = LanguageMap.getInstance().getVisualOrder(holder.dialog.getOptionText()).toArray(new IReorderingProcessor[0]);
        int box_height = text_height + 2 * MARGIN;
        int option_height = LINE_HEIGHT + 2 * MARGIN;
        main_box.setSize(this, 0, height - box_height - option_height, width, box_height, MARGIN);
        option_box = new WindowBox[options.length];
        for (int i = 0; i < option_box.length; i++) {
            int x = width * i / option_box.length;
            int w = width * (i + 1) / option_box.length - x;
            option_box[i] = new WindowBox();
            option_box[i].setSize(this, x, height - option_height, w, option_height, MARGIN);
        }
    }

    private static List<ITextProperties> splitLines(ITextComponent text, int width) {
        CharacterManager splitter = Minecraft.getInstance().font.getSplitter();
        List<ITextProperties> list = null;
        float max = 3.4028235E38F;
        for (int offset : TEST_SPLIT_OFFSETS) {
            List<ITextProperties> splitLines = splitter.splitLines(text, width - offset, Style.EMPTY);
            float diff = Math.abs(getMaxWidth(splitter, splitLines) - (float) width);
            if (diff <= 10.0F) {
                return splitLines;
            }

            if (diff < max) {
                max = diff;
                list = splitLines;
            }
        }

        return list;
    }

    private static float getMaxWidth(CharacterManager splitter, List<ITextProperties> list) {
        return (float) list.stream().mapToDouble(splitter::stringWidth).max().orElse(0.0D);
    }

    public void render(MatrixStack matrix, int mx, int my, float partial) {
        FontRenderer font = Minecraft.getInstance().font;
        fill(matrix, 0, main_box.y - MARGIN, width, height, 0x80000000);
        for (int i = 0; i < text.size(); i++) {
            font.draw(matrix, text.get(i), MARGIN + MAIN_TEXT_MARGIN, main_box.y + i * LINE_HEIGHT, 0xFFFFFFFF);
        }
        main_box.render(matrix, MARGIN, 0xFFFFFFFF, WindowBox.RenderType.MARGIN);
        main_box.render(matrix, 2, 0xFF606060, WindowBox.RenderType.MARGIN);
        for (WindowBox box : option_box) {
            box.render(matrix, MARGIN, box.isMouseIn(mx, my) ? 0xFFFFFF00 : 0xFFFFFFFF, WindowBox.RenderType.MARGIN);
            box.render(matrix, 2, 0xFF606060, WindowBox.RenderType.MARGIN);
        }
        for (int i = 0; i < options.length; i++) {
            IReorderingProcessor option = options[i];
            WindowBox box = option_box[i];
            float w = Minecraft.getInstance().font.width(option);
            font.draw(matrix, option, box.x + (box.w - w) / 2, box.y, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            for (int i = 0; i < option_box.length; i++) {
                WindowBox box = option_box[i];
                if (box.isMouseIn(mx, my)) {
                    if (holder.next(i))
                        return true;
                    else Minecraft.getInstance().setScreen(null);
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
