package com.hikarishima.lightland.config.road;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hikarishima.lightland.config.FileIO;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.DoubleFunction;

public class ImageRoadReader {

    public static Config CONFIG;
    public static BufferedImage ROAD;

    public static void init() {
        File roadfile = FileIO.loadConfigFile("road.png");
        File configfile = FileIO.loadConfigFile("road_config.json");
        ExceptionHandler.run(() -> {
            ROAD = ImageIO.read(roadfile);
            JsonElement je = new JsonParser().parse(new FileReader(configfile));
            CONFIG = Serializer.from(je.getAsJsonObject(), Config.class, null);
        });
    }

    public static void clear() {
        CONFIG = null;
        ROAD = null;
        RuralRoadBuilder.cache_road = null;
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

    public static void buildRoadSurface(int x, int z, Random r, IChunk c, BlockPos pos) {
        if (((RuralRoadBuilder.getRuralRoad(x >> 4, z >> 4)[(x & 12) + ((z >> 2) & 3)] >> (15 - (x & 3) * 4 - (z & 3)) & 1)) == 1) {
            BlockState bs;
            if (CONFIG.debug)
                if (((x & 3) == 1 || (x & 3) == 2) && ((z & 3) == 1 || (z & 3) == 2))
                    bs = Blocks.IRON_BLOCK.defaultBlockState();
                else
                    bs = Blocks.COBBLESTONE.defaultBlockState();
            else {
                double v = r.nextDouble();
                Block b = c.getBlockState(pos).getBlock();
                b = ImageRoadReader.CONFIG.getBlock(b, v);
                bs = b == null ? null : b.defaultBlockState();
            }
            if (bs != null)
                c.setBlockState(pos, bs, false);
        }
    }

    public enum RoadType {
        COMMERCIAL, COMMERCIAL_SIDE,
        COMMERCIAL_CROSS_SIDE, COMMERCIAL_CROSS_SPINE, COMMERCIAL_CROSS_CENTER,
        NONE, VILLAGE, RURAL;

        public static int getCommercial(RoadType type) {
            switch (type) {
                case COMMERCIAL:
                case COMMERCIAL_CROSS_SPINE:
                case COMMERCIAL_CROSS_CENTER:
                    return 2;
                case COMMERCIAL_SIDE:
                case COMMERCIAL_CROSS_SIDE:
                    return 1;
                default:
                    return 0;
            }
        }
    }

    @SerialClass
    public static class Config {

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

        @SerialClass
        public static class Road {

            @SerialClass.SerialField
            public String color;

            @SerialClass.SerialField
            public RoadType type;

        }

        @SerialClass
        public static class RoadMaterial {

            @SerialClass.SerialField
            public String base_block;
            @SerialClass.SerialField
            public MaterialEntry[] entries;

            public Block get(double r) {
                int total_weight = 0;
                for (MaterialEntry m : entries) {
                    total_weight += m.weight;
                }
                int rand = (int) (total_weight * r);
                for (MaterialEntry m : entries) {
                    if (rand < m.weight)
                        return m.b;
                    rand -= m.weight;
                }
                return null;
            }

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

        }

    }

}
