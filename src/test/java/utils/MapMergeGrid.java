package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class MapMergeGrid {

    public static void main(String[] args) throws Exception {
        File file;
        file = new File("./doc/map.png");
        BufferedImage map = ImageIO.read(file);
        int mw = map.getWidth();
        int mh = map.getHeight();
        file = new File("./doc/fix.png");
        BufferedImage img = ImageIO.read(file);
        int w = img.getWidth();
        int h = img.getHeight();
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++) {
                int col = img.getRGB(i, j) & 0xFFFFFF;
                if (col != 0x678284 && col != 0xD8B381 && col != 0x797876 && col != 0x402E0A) {
                    int cmap = map.getRGB(i % mw, j % mh) & 0xFFFFFF;
                    int x1 = i / mw;
                    int y1 = j / mh;
                    int c0 = getColor(x1, y1);
                    int c1 = getColor(x1 + 1, y1);
                    int c2 = getColor(x1, y1 + 1);
                    int crep = cmap == 0xFFFFFF ? c0 : cmap == 0x000000 ? c1 : c2;
                    img.setRGB(i, j, getMiddle(col, crep) | 0xFF000000);
                }
            }
        file = new File("./doc/merge.png");
        if (!file.exists())
            file.createNewFile();
        ImageIO.write(img, "PNG", file);
    }

    public static int getColor(int x, int y) {
        return x % 2 == 0 ? y % 2 == 0 ? 0xFF8080 : 0x80FF80 : y % 2 == 0 ? 0x8080FF : 0x808080;
    }

    public static int getMiddle(int c0, int c1) {
        int r0 = c0 >> 16 & 0xFF;
        int r1 = c1 >> 16 & 0xFF;
        int g0 = c0 >> 8 & 0xFF;
        int g1 = c1 >> 8 & 0xFF;
        int b0 = c0 & 0xFF;
        int b1 = c1 & 0xFF;
        int r = Math.round(r0 * 0.7f + r1 * 0.3f);
        int g = Math.round(g0 * 0.7f + g1 * 0.3f);
        int b = Math.round(b0 * 0.7f + b1 * 0.3f);
        return r << 16 | g << 8 | b;
    }

}
