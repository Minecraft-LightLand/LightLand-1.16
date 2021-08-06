package com.hikarishima.lightland.config.worldgen;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hikarishima.lightland.config.FileIO;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ImageBiomeReader {

    public static BufferedImage BIOME, DEPTH, SCALE;
    public static Config CONFIG;

    public static void init() {
        File imgfile = FileIO.loadConfigFile("biome.png");
        File configfile = FileIO.loadConfigFile("biome_config.json");
        ExceptionHandler.run(() -> {
            BIOME = ImageIO.read(imgfile);
            JsonElement je = new JsonParser().parse(new FileReader(configfile));
            CONFIG = Serializer.from(je.getAsJsonObject(), Config.class, null);
        });
        File file_depth = FileIO.getFile("depth.png");
        File file_scale = FileIO.getFile("scale.png");
        if (file_depth.exists() && file_scale.exists()) {
            ExceptionHandler.run(() -> {
                DEPTH = ImageIO.read(file_depth);
                SCALE = ImageIO.read(file_scale);
            });
        }
    }

    public static void clear() {
        BIOME = null;
        DEPTH = null;
        SCALE = null;
        CONFIG = null;
    }

    public static ResourceLocation getBiome(int x, int z) {
        if (BIOME == null || CONFIG == null)
            return null;

        if (x < 0 || x >= BIOME.getWidth() || z < 0 || z >= BIOME.getHeight())
            return null;

        int val = BIOME.getRGB(x, z) & 0x00FFFFFF;
        if (!CONFIG.map.containsKey(val)) {
            LogManager.getLogger().error("color " + Integer.toString(val, 16) + " not mapped. Keyset: " +
                    CONFIG.map.keySet().stream().map(e -> Integer.toString(e, 16)).collect(Collectors.toList()));
            return null;
        }
        return CONFIG.map.get(val);
    }

    private static float getDepthImpl(int x, int z) {
        if (DEPTH == null)
            return Float.NaN;
        if (x < 0 || x >= DEPTH.getWidth() || z < 0 || z >= DEPTH.getHeight())
            return Float.NaN;
        int col = DEPTH.getRGB(x, z) & 0xFF;
        return col / 16f - 4f;
    }

    private static float getScaleImpl(int x, int z) {
        if (SCALE == null)
            return Float.NaN;
        if (x < 0 || x >= SCALE.getWidth() || z < 0 || z >= SCALE.getHeight())
            return Float.NaN;
        int col = SCALE.getRGB(x, z) & 0xFF;
        return col / 512f;
    }

    public static float getDepth(BiomeProvider pvd, int x, int z) {
        float ans = getDepthImpl(x, z);
        if (Float.isNaN(ans))
            return pvd.getNoiseBiome(x, 0, z).getDepth();
        return ans;
    }

    public static float getScale(BiomeProvider pvd, int x, int z) {
        float ans = getScaleImpl(x, z);
        if (Float.isNaN(ans))
            return pvd.getNoiseBiome(x, 0, z).getScale();
        return ans;
    }

    public static void genGradient() {
        if (BIOME == null || CONFIG == null)
            return;
        File file_depth = FileIO.getFile("depth.png");
        File file_scale = FileIO.getFile("scale.png");

        if (file_depth.exists() && file_scale.exists())
            return;
        BufferedImage img_depth = loadOrCreate(file_depth);
        BufferedImage img_scale = loadOrCreate(file_scale);

        for (int x = 0; x < BIOME.getWidth(); x++)
            for (int z = 0; z < BIOME.getHeight(); z++) {
                ResourceLocation rl = getBiome(x, z);
                Biome biome = null;
                if (rl != null)
                    biome = ForgeRegistries.BIOMES.getValue(rl);
                if (biome == null)
                    biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation("minecraft:deep_ocean"));
                int depth = MathHelper.clamp(Math.round((biome.getDepth() + 4) * 16), 0, 255);
                int scale = MathHelper.clamp(Math.round(biome.getScale() * 512), 0, 255);
                img_depth.setRGB(x, z, depth << 16 | depth << 8 | depth);
                img_scale.setRGB(x, z, scale << 16 | scale << 8 | scale);
            }
        FileIO.checkFile(file_depth);
        FileIO.checkFile(file_scale);
        ExceptionHandler.run(() -> {
            ImageIO.write(img_depth, "PNG", file_depth);
            ImageIO.write(img_scale, "PNG", file_scale);
        });

    }

    private static BufferedImage loadOrCreate(File file) {
        return file.exists() ? ExceptionHandler.get(() -> ImageIO.read(file)) :
                new BufferedImage(BIOME.getWidth(), BIOME.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    }

    @SerialClass
    public static class Config {

        @SerialClass.SerialField
        public Entry[] entries;
        public Map<Integer, ResourceLocation> map = new HashMap<>();

        @SerialClass.OnInject
        public void onInject() {
            for (Entry e : entries) {
                int color = Integer.parseInt(e.color, 16);
                ResourceLocation rl = new ResourceLocation(e.id);
                map.put(color, rl);
            }
        }

        @SerialClass
        public static class Entry {

            @SerialClass.SerialField
            public String color;

            @SerialClass.SerialField
            public String id;

        }

    }

}
