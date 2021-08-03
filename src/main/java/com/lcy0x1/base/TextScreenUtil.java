package com.lcy0x1.base;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.*;

import java.util.List;

public class TextScreenUtil {

    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};

    public static List<ITextProperties> splitLines(ITextComponent text, int width) {
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

}
