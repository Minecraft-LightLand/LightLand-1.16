package layer;

import layer.filter.MagnifyFilter;
import layer.filter.SmoothFilter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class GenNoise {

    public static int N = 5, w0 = 32, h0 = 32;

    public static void main(String[] args) throws Exception {
        int[] cols = new int[N];
        for (int i = 0; i < N; i++) {
            cols[i] = new Random().nextInt() & 0xFFFFFF;
        }
        Integer[][] map0 = new Integer[w0][h0];
        for (int i = 0; i < w0; i++) {
            for (int j = 0; j < h0; j++) {
                map0[i][j] = (int) (Math.random() * N);
            }
        }
        Random r = new Random();
        MagnifyFilter<Integer> noise = new MagnifyFilter<>(r, MagnifyFilter.Type.NOISE);
        MagnifyFilter<Integer> smooth = new MagnifyFilter<>(r, MagnifyFilter.Type.SMOOTH);
        SmoothFilter<Integer> filter = new SmoothFilter<>(r, SmoothFilter.Type.ALL);

        map0 = noise.process(map0);
        map0 = filter.process(map0);
        map0 = noise.process(map0);
        map0 = filter.process(map0);
        map0 = smooth.process(map0);
        map0 = filter.process(map0);
        map0 = smooth.process(map0);
        map0 = filter.process(map0);

        BufferedImage img = new BufferedImage(w0, h0, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < w0; i++) {
            for (int j = 0; j < h0; j++) {
                img.setRGB(i, j, 0xFF000000 | cols[map0[i][j]]);
            }
        }
        File file = new File("./doc/ignore/noise.png");
        if (!file.exists())
            file.createNewFile();
        ImageIO.write(img, "PNG", file);
    }

}
