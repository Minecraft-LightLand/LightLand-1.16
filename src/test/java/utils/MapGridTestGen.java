package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class MapGridTestGen {

    public static void main(String[] args) throws Exception {
        File file = new File("./doc/map.png");
        BufferedImage img = ImageIO.read(file);
        File out = new File("./doc/grid.png");
        if (!out.exists())
            out.createNewFile();
        int r0 = 32, r1 = 128;
        BufferedImage ans = new BufferedImage(r0 * r1, r0 * r1, BufferedImage.TYPE_BYTE_INDEXED);
        for (int x0 = 0; x0 < r0; x0++)
            for (int y0 = 0; y0 < r0; y0++) {
                int col = img.getRGB(x0, y0) & 0xFFFFFF;
                for (int x1 = 0; x1 < r1; x1++)
                    for (int y1 = 0; y1 < r1; y1++) {
                        int x = x1 * r0 + x0;
                        int y = y1 * r0 + y0;
                        int c0 = getColor(x1, y1);
                        int c1 = getColor(x1 + 1, y1);
                        int c2 = getColor(x1, y1 + 1);
                        int c = col == 0xFFFFFF ? c0 : col == 0x000000 ? c1 : c2;
                        ans.setRGB(x, y, c);
                    }
            }
        ImageIO.write(ans, "PNG", out);
    }

    public static int getColor(int x, int y) {
        return x % 2 == 0 ? y % 2 == 0 ? 0xFF8080 : 0x80FF80 : y % 2 == 0 ? 0x8080FF : 0x80FFFF;
    }

}
