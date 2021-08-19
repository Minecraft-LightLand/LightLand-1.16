package utils;

import com.google.common.collect.Maps;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

public class Minimizer {

    public static final int W = 4096;
    public static final int H = 4096;

    public static void main(String[] args) throws Exception {
        File file;
        file = new File("./doc/ignore/fix.png");
        BufferedImage map = ImageIO.read(file);
        int mw = map.getWidth();
        int mh = map.getHeight();
        int x0 = (W - mw) / 2;
        int y0 = (H - mh) / 2;
        int[][] map0 = new int[W][H];
        for (int i = 0; i < W; i++) {
            for (int j = 0; j < H; j++) {
                int x = i - x0;
                int y = j - y0;
                if (x < 0 || x >= mw || y < 0 || y >= mh)
                    map0[i][j] = map.getRGB(0, 0);
                else map0[i][j] = map.getRGB(x, y);
            }
        }
        int w = W;
        int h = H;
        int n = 32;
        w /= n;
        h /= n;
        BufferedImage ans = new BufferedImage(w, h, map.getType());
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Map<Integer, Integer> set = Maps.newLinkedHashMap();
                for (int j1 = 0; j1 < n; j1++) {
                    for (int k1 = 0; k1 < n; k1++) {
                        int val = map0[i * n + j1][j * n + k1];
                        set.put(val, set.getOrDefault(val, 0) + 1);
                    }
                }
                int col = set.entrySet().stream().sorted((a, b) -> b.getValue().compareTo(a.getValue())).findFirst().get().getKey();
                ans.setRGB(i, j, col);
            }
        }
        File out = new File("./doc/ignore/minify.png");
        if (!out.exists())
            out.createNewFile();
        ImageIO.write(ans, "PNG", out);
    }


    private static int max(int... vals) {
        Map<Integer, Integer> map = Maps.newLinkedHashMap();
        for (int v : vals) {
            map.put(v, map.getOrDefault(v, 0) + 1);
        }
        int val = vals[0];
        for (int v : vals) {
            if (map.getOrDefault(v, 0) > map.getOrDefault(val, 0)) {
                val = v;
            }
        }
        return val;
    }

}
