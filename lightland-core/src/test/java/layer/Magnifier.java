package layer;

import com.google.gson.JsonParser;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import layer.assembler.CoverAssembler;
import layer.filter.DiffFilter;
import layer.filter.MagnifyFilter;
import layer.filter.RemapFilter;
import layer.filter.SmoothFilter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Magnifier {

    public static final int OCEAN = 0x678284;
    public static final int SNOW = 0xD0DBDF;
    public static final int STONE = 0x797876;
    public static final int PLAINS = 0xA5B049;
    public static final int FOREST = 0x607013;
    public static final int HILLS = 0x402E0A;
    public static final int HIGH_PLAINS = 0x868E4B;
    public static final int HIGH_LAND = 0xA47349;
    public static final int DESERT = 0xB39467;
    public static final int SWAMP = 0x6F4C27;
    public static final int DEEP = 0x4E5B8C;

    public static final int BEACH = 0xD8D381;
    public static final int SHORE = 0x93B0BB;
    public static final int RIVER = 0x068791;

    public static void main(String[] args) throws Exception {
        File file;
        BufferedImage map_img = ImageIO.read(new File("./doc/ignore/minify.png"));
        BufferedImage river_img = ImageIO.read(new File("./doc/ignore/river.png"));
        int w = map_img.getWidth();
        int h = map_img.getHeight();

        File json = new File("./doc/ignore/biome.json");
        BiomeMap colors = Serializer.from(new JsonParser().parse(new FileReader(json)), BiomeMap.class, null);

        RawBiome[][] map0 = new RawBiome[w][h];
        Integer[][] river_map = new Integer[w][h];
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++) {
                int col = map_img.getRGB(i, j) & 0xFFFFFF;
                if (!colors.color_map.containsKey(col))
                    colors.color_map.put(col, new RawBiome(col));
                map0[i][j] = colors.color_map.get(col);
                col = river_img.getRGB(i, j) & 0xFFFFFF;
                river_map[i][j] = col == 0x000000 ? 1 : col == 0x808080 ? 2 : col == 0xFFFFFF ? 3 : 0;
            }


        RawBiome beach = new RawBiome(BEACH);
        RawBiome shore = new RawBiome(SHORE);
        RawBiome river = new RawBiome(RIVER);
        long seed = new Random().nextLong();
        Random filter_random = new Random(seed);
        MagnifyFilter<RawBiome> noise = new MagnifyFilter<>(filter_random, MagnifyFilter.Type.NOISE);
        SmoothFilter<RawBiome> filter = new SmoothFilter<>(filter_random, SmoothFilter.Type.ALL);

        map0 = noise.process(map0);
        map0 = filter.process(map0);

        map0 = new RemapFilter<>(RawBiome.class, colors::rand).process(map0);

        map0 = noise.process(map0);
        map0 = filter.process(map0);
        map0 = noise.process(map0);
        map0 = filter.process(map0);


        Boolean[][] river_bool;
        Random river_random = new Random(seed);
        MagnifyFilter<Integer> riv_noise = new MagnifyFilter<>(river_random, MagnifyFilter.Type.NOISE);
        MagnifyFilter<Integer> riv_smooth = new MagnifyFilter<>(river_random, MagnifyFilter.Type.SMOOTH);
        SmoothFilter<Integer> riv_filter = new SmoothFilter<>(river_random, SmoothFilter.Type.ALL);
        SmoothFilter<Boolean> bool_smooth = new SmoothFilter<>(new Random(seed), SmoothFilter.Type.SIDE);
        river_map = riv_noise.process(river_map);
        river_map = riv_filter.process(river_map);
        river_map = riv_noise.process(river_map);
        river_map = riv_filter.process(river_map);
        river_map = riv_noise.process(river_map);
        river_map = riv_filter.process(river_map);
        river_map = riv_smooth.process(river_map);
        river_map = riv_filter.process(river_map);
        river_bool = new DiffFilter<Integer>((a, b) -> a * b > 0 && !a.equals(b)).process(river_map);
        river_bool = bool_smooth.process(river_bool);


        MagnifyFilter<RawBiome> smooth = new MagnifyFilter<>(filter_random, MagnifyFilter.Type.SMOOTH);

        Boolean[][] beach_map = new DiffFilter<RawBiome>((a, b) -> a.base_biome != STONE && a.base_biome != SNOW && a.base_biome != OCEAN && a.base_biome != DEEP && b.base_biome == OCEAN).process(map0);
        Boolean[][] shore_map = new DiffFilter<RawBiome>((a, b) -> (a.base_biome == STONE || a.base_biome == SNOW) && b.base_biome == OCEAN).process(map0);

        map0 = new CoverAssembler<>(beach).assemble(map0, beach_map);
        map0 = new CoverAssembler<>(shore).assemble(map0, shore_map);

        map0 = smooth.process(map0);
        map0 = filter.process(map0);

        map0 = new CoverAssembler<>(river).assemble(map0, river_bool);
        Boolean[][] river_expand = new DiffFilter<RawBiome>((a, b) -> a != river && a.base_biome != OCEAN && b == river).process(map0);
        map0 = new CoverAssembler<>(river).assemble(map0, river_expand);
        Boolean[][] river_shrink = new DiffFilter<RawBiome>((a, b) -> a == river && b != river && b.base_biome != OCEAN && b.base_biome != STONE && b.base_biome != HILLS).process(map0);
        map0 = new CoverAssembler<>(beach).assemble(map0, river_shrink);


        map0 = smooth.process(map0);
        map0 = filter.process(map0);

        Integer[][] cmap = new RemapFilter<RawBiome, Integer>(Integer.class, b -> b.variant).process(map0);

        int w0 = w * 32;
        int h0 = h * 32;
        BufferedImage img = new BufferedImage(w0, h0, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < w0; i++) {
            for (int j = 0; j < h0; j++) {
                img.setRGB(i, j, 0xFF000000 | cmap[i][j]);
            }
        }
        file = new File("./doc/ignore/re_noise.png");
        if (!file.exists())
            file.createNewFile();
        ImageIO.write(img, "PNG", file);
    }

    public static class RawBiome {

        public int base_biome;
        public int variant;

        public RawBiome(int col) {
            base_biome = col;
            variant = col;
        }

        public RawBiome(RawBiome parent, int col) {
            base_biome = parent.base_biome;
            variant = col;
        }

    }

    @SerialClass
    public static class BiomeMap {

        private final Map<Integer, RawBiome> color_map = new HashMap<>();
        private final Map<RawBiome, List<RawBiome>> map = new HashMap<>();

        @SerialClass.SerialField(generic = {String.class, String[].class})
        public HashMap<String, String[]> json;

        @SerialClass.OnInject
        public void onInject() {
            json.forEach((k, v) -> {
                int ik = Integer.valueOf(k, 16);
                RawBiome biome = new RawBiome(ik);
                color_map.put(ik, biome);
                List<RawBiome> list = new ArrayList<>();
                for (String vi : v) {
                    int iv = Integer.valueOf(vi, 16);
                    RawBiome sub = new RawBiome(biome, iv);
                    list.add(sub);
                }
                map.put(biome, list);
            });
        }


        public RawBiome rand(RawBiome b) {
            List<RawBiome> list = map.get(b);
            if (list == null) {
                return b;
            }
            int n = list.size();
            return list.get((int) (Math.random() * n));
        }
    }

}
