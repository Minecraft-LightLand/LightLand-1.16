package com.hikarishima.lightland.terrain.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hikarishima.lightland.config.FileIO;
import com.hikarishima.lightland.terrain.LightLandTerrain;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;

import java.io.File;
import java.io.FileReader;

public class VolcanoBiomeReader {

    public static VolcanoConfig CONFIG;

    public static void init() {
        if (CONFIG != null)
            return;
        File configfile = FileIO.loadConfigFile(LightLandTerrain.MODID,"volcano_config.json");
        ExceptionHandler.run(() -> {
            JsonElement je = new JsonParser().parse(new FileReader(configfile));
            CONFIG = Serializer.from(je.getAsJsonObject(), VolcanoConfig.class, null);
        });
    }

    @SerialClass
    public static class VolcanoConfig {

        @SerialClass.SerialField
        public int lava_level, side_count;
        @SerialClass.SerialField
        public float max, step, scale;
        @SerialClass.SerialField
        public LavaWell lava_well;

        @SerialClass
        public static class LavaWell {

            @SerialClass.SerialField
            public float size, height, delta_size, delta_height, slope, delta_slope;

            @SerialClass.SerialField
            public float chance, lava_lake_chance;

        }

    }

}
