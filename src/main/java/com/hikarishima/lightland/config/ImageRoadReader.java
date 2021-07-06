package com.hikarishima.lightland.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleFunction;

public class ImageRoadReader {

    public enum RoadType {
        NONE, COMMERCE, VILLAGE, RURAL
    }

    @SerialClass
    public static class Config {

        @SerialClass
        public static class Road {

            @SerialClass.SerialField
            public String color;

            @SerialClass.SerialField
            public RoadType type;

        }

        @SerialClass
        public static class RoadMaterial {

            @SerialClass
            public static class MaterialEntry {

                @SerialClass.SerialField
                public String block;

                @SerialClass.SerialField
                public int weight;

                public Block b;

                @SerialClass.OnInject
                public void onInject() {
                    if (block != null) {
                        ResourceLocation rl = new ResourceLocation(block);
                        b = ForgeRegistries.BLOCKS.getValue(rl);
                        if (b == null)
                            LogManager.getLogger().error("block " + block + " not exist");
                    }
                }

            }

            @SerialClass.SerialField
            public String base_block;

            @SerialClass.SerialField
            public RoadMaterial.MaterialEntry[] entries;

            public Block get(double r) {
                int total_weight = 0;
                for (RoadMaterial.MaterialEntry m : entries) {
                    total_weight += m.weight;
                }
                int rand = (int) (total_weight * r);
                for (RoadMaterial.MaterialEntry m : entries) {
                    if (rand < m.weight)
                        return m.b;
                    rand -= m.weight;
                }
                return null;
            }

        }

        @SerialClass.SerialField
        public boolean debug;

        @SerialClass.SerialField
        public Road[] roads;

        @SerialClass.SerialField
        public RoadMaterial[] materials;

        public Map<Integer, RoadType> roadmap = new HashMap<>();
        public Map<Block, DoubleFunction<Block>> blockmap = new HashMap<>();

        @SerialClass.OnInject
        public void onInject() {
            for (Road e : roads) {
                int color = Integer.parseInt(e.color, 16);
                roadmap.put(color, e.type);
            }
            for (RoadMaterial e : materials) {
                Block b = null;
                if (e.base_block != null) {
                    ResourceLocation rl = new ResourceLocation(e.base_block);
                    b = ForgeRegistries.BLOCKS.getValue(rl);
                }
                blockmap.put(b, e::get);
            }
        }

        @Nullable
        public Block getBlock(Block b, double x) {
            DoubleFunction<Block> ans = blockmap.get(b);
            if (ans == null)
                ans = blockmap.get(null);
            return ans.apply(x);
        }

    }

    private static final int FILL_UL = 0b1110111011000000;
    private static final int FILL_UP = 0b1111111101100000;
    private static final int FILL_SUL = 0b1100100000000000;
    private static final int FILL_UCW = 0b1111111100110000;
    private static final int FILL_UCCW = 0b1111111111000000;

    private static int rotate(int fill, int a) {
        if (a == 0)
            return fill;
        else if (a == 1) {
            int ans = 0;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++)
                    ans |= ((fill >> (15 - i * 4 - j)) & 1) << (12 - j * 4 + i);
            return ans;
        } else if (a == 2) {
            int ans = 0;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++)
                    ans |= ((fill >> (15 - i * 4 - j)) & 1) << (i * 4 + j);
            return ans;
        } else {
            int ans = 0;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++)
                    ans |= ((fill >> (15 - i * 4 - j)) & 1) << (3 + j * 4 - i);
            return ans;
        }
    }

    private enum ConnDire {
        UL(-1, -1, 0),
        UP(-1, 0, 1),
        UR(-1, 1, 2),
        LEFT(0, -1, 7),
        RIGHT(0, 1, 3),
        DL(1, -1, 6),
        DOWN(1, 0, 5),
        DR(1, 1, 4);

        public static final ConnDire[] ADJS = {UL, UP, UR, RIGHT, DR, DOWN, DL, LEFT};

        public final int dx, dz, a, fill;

        ConnDire(int dx, int dz, int a) {
            this.dx = dx;
            this.dz = dz;
            this.a = a;
            this.fill = ImageRoadReader.rotate(a % 2 == 0 ? FILL_UL : FILL_UP, a / 2);
        }

        public boolean isDiag() {
            return dx * dz != 0;
        }

        public ConnDire getHor() {
            return getDire(dx, 0);
        }

        public ConnDire getVer() {
            return getDire(0, dz);
        }

        public ConnDire rotate(int ind) {
            return ADJS[(a + ind + 8) % 8];
        }

        public int mask() {
            return 1 << ordinal();
        }

        public boolean isIn(int mask) {
            return (mask & mask()) != 0;
        }

        public static ConnDire getDire(int dx, int dz) {
            int a = (dx + 1) * 3 + (dz + 1);
            if (a > 3) a--;
            return ConnDire.values()[a];
        }
    }

    private enum SideDire {
        SUL(-1, -1),
        SUR(-1, 1),
        SDR(1, 1),
        SDL(1, -1);

        public final int dx, dz, fill;

        SideDire(int dx, int dz) {
            this.dx = dx;
            this.dz = dz;
            this.fill = ImageRoadReader.rotate(FILL_SUL, ordinal());
        }

        public ConnDire getHor() {
            return ConnDire.getDire(dx, 0);
        }

        public ConnDire getVer() {
            return ConnDire.getDire(0, dz);
        }

    }

    private enum SpecDire {
        UCW(-1, 0),
        UCCW(-1, 0),
        RCW(0, 1),
        RCCW(0, 1),
        DCW(1, 0),
        DCCW(1, 0),
        LCW(0, -1),
        LCCW(0, -1);

        public final int dx, dz, fill;

        SpecDire(int dx, int dz) {
            this.dx = dx;
            this.dz = dz;
            this.fill = ImageRoadReader.rotate(ordinal() % 2 == 0 ? FILL_UCW : FILL_UCCW, ordinal() / 2);
        }

    }

    public static Config CONFIG;
    public static BufferedImage ROAD;

    private static int cache_x, cache_z;
    private static int[] cache_val;

    public static void init() {
        File roadfile = FileIO.loadConfigFile("road.png");
        File configfile = FileIO.loadConfigFile("road_config.json");
        ExceptionHandler.run(() -> {
            ROAD = ImageIO.read(roadfile);
            JsonElement je = new JsonParser().parse(new FileReader(configfile));
            CONFIG = Serializer.from(je.getAsJsonObject(), Config.class, null);
        });
    }

    public static RoadType getRoadPixel(int px, int pz) {
        if (ROAD == null || CONFIG == null)
            return RoadType.NONE;
        if (px < 0 || px >= ROAD.getWidth())
            return RoadType.NONE;
        if (pz < 0 || pz >= ROAD.getHeight())
            return RoadType.NONE;
        RoadType type = CONFIG.roadmap.get(ROAD.getRGB(px, pz) & 0x00FFFFFF);
        if (type != null)
            return type;
        return RoadType.NONE;
    }

    private static int special_road(int types) {
        for (int i = 0; i < 4; i++) {
            ConnDire cd = ConnDire.ADJS[i * 2 + 1];
            if (!cd.isDiag() && cd.isIn(types)) {
                int cw = cd.mask() | cd.rotate(-1).mask() | cd.rotate(2).mask();
                int ccw = cd.mask() | cd.rotate(1).mask() | cd.rotate(-2).mask();
                if (types == cw)
                    return SpecDire.values()[i * 2].fill;
                if (types == ccw)
                    return SpecDire.values()[i * 2 + 1].fill;
            }
        }
        return -1;
    }

    private static int node_road(int types) {
        int conn = 0;
        int mask = 0;
        for (ConnDire cd : ConnDire.ADJS)
            if (cd.isDiag() && cd.isIn(types) && cd.getHor().isIn(types) && cd.getVer().isIn(types)) {
                conn |= cd.fill;
                mask |= cd.mask() | cd.getHor().mask() | cd.getVer().mask();
            }
        types = (types | mask) - mask;
        mask = 0;
        for (ConnDire cd : ConnDire.ADJS)
            if (!cd.isDiag() && cd.isIn(types)) {
                conn |= cd.fill;
                mask |= cd.mask() | cd.getHor().mask() | cd.getVer().mask();
            }
        types = (types | mask) - mask;
        for (ConnDire cd : ConnDire.ADJS)
            if (cd.isDiag() && cd.isIn(types)) {
                conn |= cd.fill;
            }
        return conn;
    }

    public static int[] getRuralRoad(int x, int z) {
        if (cache_x == x && cache_z == z && cache_val != null)
            return cache_val;

        RoadType[][] area = new RoadType[6][6];
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 6; j++)
                area[i][j] = getRoadPixel(x * 4 - 1 + i, z * 4 - 1 + j);

        int[] ans = new int[16];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                int types = 0;
                for (ConnDire cd : ConnDire.ADJS) {
                    if (area[i + 1 + cd.dx][j + 1 + cd.dz] == RoadType.RURAL) {
                        types |= cd.mask();
                    }
                }
                if (area[i + 1][j + 1] == RoadType.RURAL) {
                    int special = special_road(types);
                    ans[i * 4 + j] = special >= 0 ? special : node_road(types);
                } else if (area[i + 1][j + 1] == RoadType.NONE) {
                    int side = 0;
                    for (SideDire cd : SideDire.values())
                        if (cd.getHor().isIn(types) && cd.getVer().isIn(types)) {
                            side |= cd.fill;
                        }
                    ans[i * 4 + j] = side;
                }
            }

        cache_x = x;
        cache_z = z;
        cache_val = ans;
        return ans;
    }

    public static boolean onRoad(int x, int z) {
        int[] roads = getRuralRoad(x >> 4, z >> 4);
        int cx = x & 15;
        int cz = z & 15;
        int road = roads[cx / 4 * 4 + cz / 4];
        return ((road >> (15 - (cx & 3) * 4 - (cz & 3)) & 1)) == 1;
    }

}
