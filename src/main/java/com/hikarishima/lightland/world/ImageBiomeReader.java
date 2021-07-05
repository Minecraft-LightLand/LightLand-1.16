package com.hikarishima.lightland.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hikarishima.lightland.FileIO;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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

public class ImageBiomeReader {

    @SerialClass
    public static class Config {

        @SerialClass
        public static class Entry {

            @SerialClass.SerialField
            public String color;

            @SerialClass.SerialField
            public String id;

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

        }

        @SerialClass.SerialField
        public int accuracy, xoffset, yoffset, road_type;

        @SerialClass.SerialField
        public Entry[] entries;

        @SerialClass.SerialField
        public Road[] roads;

        @SerialClass.SerialField
        public RoadMaterial[] materials;

        public Map<Integer, ResourceLocation> map = new HashMap<>();
        public Map<Integer, RoadType> roadmap = new HashMap<>();
        public Map<Block, DoubleFunction<Block>> blockmap = new HashMap<>();

        @SerialClass.OnInject
        public void onInject() {
            for (Entry e : entries) {
                int color = Integer.parseInt(e.color, 16);
                ResourceLocation rl = new ResourceLocation(e.id);
                map.put(color, rl);
            }
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

    public enum RoadType {
        NONE, COMMERCE, VILLAGE, RURAL
    }

    public static BufferedImage BIOME, ROAD;
    public static Config CONFIG;

    public static void init() {
        File imgfile = FileIO.loadConfigFile("biome.png");
        File roadfile = FileIO.loadConfigFile("road.png");
        File configfile = FileIO.loadConfigFile("biome_config.json");
        ExceptionHandler.run(() -> {
            BIOME = ImageIO.read(imgfile);
            ROAD = ImageIO.read(roadfile);
            JsonElement je = new JsonParser().parse(new FileReader(configfile));
            CONFIG = Serializer.from(je.getAsJsonObject(), Config.class, null);
        });
    }

    public static ResourceLocation getBiome(int x, int z) {
        if (BIOME == null || CONFIG == null)
            return null;

        int px = (x - CONFIG.xoffset) / CONFIG.accuracy;
        int pz = (z - CONFIG.yoffset) / CONFIG.accuracy;
        px = MathHelper.clamp(px, 0, BIOME.getWidth() - 1);
        pz = MathHelper.clamp(pz, 0, BIOME.getHeight() - 1);

        int val = BIOME.getRGB(px, pz) & 0x00FFFFFF;
        if (!CONFIG.map.containsKey(val)) {
            LogManager.getLogger().error("color " + val + " not mapped. Keyset: " + CONFIG.map.keySet());
            return null;
        }
        return CONFIG.map.get(val);
    }

    public static RoadType getRoadPixel(int x, int z) {
        if (ROAD == null || CONFIG == null)
            return RoadType.NONE;
        int px = (x - CONFIG.xoffset) / CONFIG.accuracy;
        int pz = (z - CONFIG.yoffset) / CONFIG.accuracy;
        if (px < 0 || px >= ROAD.getWidth())
            return RoadType.NONE;
        if (pz < 0 || pz >= ROAD.getHeight())
            return RoadType.NONE;
        RoadType type = CONFIG.roadmap.get(ROAD.getRGB(px, pz) & 0x00FFFFFF);
        if (type != null)
            return type;
        return RoadType.NONE;
    }

}
