package com.lcy0x1.test;

import com.hikarishima.lightland.magic.spell.magic.DirtWallSpell;
import net.minecraft.world.biome.BiomeColors;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GenericTest {

    public static void main(String[] args) throws Exception{
        File file = new File("./doc/color_test.png");
        BufferedImage img = ImageIO.read(file);
        int w = img.getWidth();
        int h = img.getHeight();
        Map<Integer, Integer> colors = new HashMap<>();
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++) {
                int col = img.getRGB(i, j);
                if (colors.containsKey(col)) {
                    colors.put(col, colors.get(col) + 1);
                } else colors.put(col, 1);
            }
        System.out.println(colors.keySet().stream().map(e->Integer.toUnsignedString(e,16)).collect(Collectors.toList()));
    }

}
