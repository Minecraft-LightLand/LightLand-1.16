package com.hikarishima.lightland.magic.gui.hex;

import com.hikarishima.lightland.config.Translator;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;

public class HexStatus {

    public enum Save {
        YES(TextFormatting.GREEN), NO(TextFormatting.RED);

        public final TextFormatting col;

        Save(TextFormatting col) {
            this.col = col;
        }

        public IFormattableTextComponent getDesc() {
            return Translator.get("screen.hex.save." + name().toLowerCase());
        }

        public int getColor() {
            return col.getColor();
        }

    }

    public enum Compile {
        COMPLETE(TextFormatting.GREEN),
        FAILED(TextFormatting.DARK_PURPLE),
        EDITING(TextFormatting.BLUE),
        ERROR(TextFormatting.RED);

        public final TextFormatting col;

        Compile(TextFormatting col) {
            this.col = col;
        }

        public IFormattableTextComponent getDesc() {
            return Translator.get("screen.hex.compile." + name().toLowerCase());
        }

        public int getColor() {
            return col.getColor();
        }

    }

}
