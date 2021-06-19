package com.hikarishima.lightland;

import com.hikarishima.lightland.mobspawn.IMobLevel;
import com.lcy0x1.core.util.ExceptionHandler;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileIO {

    public static File loadConfigFile(String name) {
        String path = FMLPaths.CONFIGDIR.get().toString();
        File file = new File(path + File.separator + LightLand.MODID + File.separator + name);
        if (!file.exists()) {
            ExceptionHandler.run(() -> {
                String jar_path = "/data/" + LightLand.MODID + "/default_config/" + name;
                InputStream is = IMobLevel.class.getResourceAsStream(jar_path);
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                file.createNewFile();
                if (is != null)
                    Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            });
        }
        return file;
    }

}
