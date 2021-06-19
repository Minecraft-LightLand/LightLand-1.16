package com.hikarishima.lightland.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

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

        @SerialClass.SerialField
        public int accuracy, xoffset, yoffset;

        @SerialClass.SerialField
        public Entry[] entries;

        public Map<Integer, Biome> map = new HashMap<>();

        @SerialClass.OnInject
        public void onInject() {
            for (Entry e : entries) {
                int color = Integer.parseInt(e.color);
                ResourceLocation rl = new ResourceLocation(e.id);
                Biome b = ForgeRegistries.BIOMES.getValue(rl);
                if (b == null)
                    LogManager.getLogger().error("biome " + e.id + " does not exist");
                else map.put(color, b);
            }
        }

    }

    public static BufferedImage IMG;
    public static Config CONFIG;

    public static void init() {
        String path = FMLPaths.CONFIGDIR.toString();
        File imgfile = new File(path + "/lightland/biome.jpg");
        File configfile = new File(path + "/lightland/biome_config.json");
        if (!imgfile.exists()) {
        } else {
            ExceptionHandler.run(() -> {
                IMG = ImageIO.read(imgfile);
            });
        }
        if (!configfile.exists()) {
        } else {
            ExceptionHandler.run(() -> {
                JsonElement je = new JsonParser().parse(new FileReader(configfile));
                CONFIG = Serializer.from(je.getAsJsonObject(), Config.class, null);
            });
        }
    }

    public static Biome getBiome(int x, int y) {
        if (IMG == null || CONFIG == null)
            return null;

        // TODO interpolate
        int px = (x - CONFIG.xoffset) / CONFIG.accuracy;
        int py = (y - CONFIG.yoffset) / CONFIG.accuracy;
        px = MathHelper.clamp(px, 0, IMG.getWidth());
        py = MathHelper.clamp(py, 0, IMG.getHeight());

        int val = IMG.getRGB(px, py);
        return CONFIG.map.get(val);
    }


}
