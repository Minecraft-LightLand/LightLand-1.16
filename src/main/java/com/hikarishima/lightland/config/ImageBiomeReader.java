package com.hikarishima.lightland.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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

    }

    public static BufferedImage BIOME;
    public static Config CONFIG;

    public static void init() {
        File imgfile = FileIO.loadConfigFile("biome.png");
        File configfile = FileIO.loadConfigFile("biome_config.json");
        ExceptionHandler.run(() -> {
            BIOME = ImageIO.read(imgfile);
            JsonElement je = new JsonParser().parse(new FileReader(configfile));
            CONFIG = Serializer.from(je.getAsJsonObject(), Config.class, null);
        });
    }

    public static ResourceLocation getBiome(int x, int z) {
        if (BIOME == null || CONFIG == null)
            return null;

        x = MathHelper.clamp(x, 0, BIOME.getWidth() - 1);
        z = MathHelper.clamp(z, 0, BIOME.getHeight() - 1);

        int val = BIOME.getRGB(x, z) & 0x00FFFFFF;
        if (!CONFIG.map.containsKey(val)) {
            LogManager.getLogger().error("color " + val + " not mapped. Keyset: " + CONFIG.map.keySet());
            return null;
        }
        return CONFIG.map.get(val);
    }

}
