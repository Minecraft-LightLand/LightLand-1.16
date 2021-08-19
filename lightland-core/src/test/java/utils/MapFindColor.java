package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MapFindColor {

    public static void main(String[] args) throws Exception {
        File file = new File("./doc/merge.png");
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
    }

}
