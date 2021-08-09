package test;

import com.hikarishima.lightland.config.worldgen.ImageBiomeReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DepthTest {

    public static void main(String[] args) throws Exception {
        File file = new File("./doc/ignore/depth.png");
        BufferedImage img = ImageIO.read(file);
        int w = img.getWidth();
        int h = img.getHeight();
        ImageBiomeReader.DEPTH = img;
        BufferedImage ans = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        int[] set = new int[16];
        int[] bin = new int[16];
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++) {
                bin[(img.getRGB(i, j) & 0xFF) / 16]++;
                float vol = ImageBiomeReader.getDepth(null, i, j);
                int val = Math.round((vol + 4));
                set[val]++;
                ans.setRGB(i, j, img.getRGB(i, j));
            }
        System.out.println(Arrays.stream(set).mapToObj(e -> e).collect(Collectors.toList()));
        System.out.println(Arrays.stream(bin).mapToObj(e -> e).collect(Collectors.toList()));
        File out = new File("./doc/ignore/out.png");
        if (!out.exists())
            out.createNewFile();
        ImageIO.write(ans, "PNG", out);
    }


}

