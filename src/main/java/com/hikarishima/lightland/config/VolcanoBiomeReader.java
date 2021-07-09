package com.hikarishima.lightland.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;

import java.io.File;
import java.io.FileReader;

public class VolcanoBiomeReader {

    @SerialClass
    public static class VolcanoConfig {

        @SerialClass.SerialField
        public int lava_level, side_count;

        @SerialClass.SerialField
        public float max, step, scale;

    }

    public static VolcanoConfig CONFIG;

    public static void init() {
        if (CONFIG != null)
            return;
        File configfile = FileIO.loadConfigFile("volcano_config.json");
        ExceptionHandler.run(() -> {
            JsonElement je = new JsonParser().parse(new FileReader(configfile));
            CONFIG = Serializer.from(je.getAsJsonObject(), VolcanoConfig.class, null);
        });
    }

    public static void clear(){
        CONFIG = null;
    }

}
