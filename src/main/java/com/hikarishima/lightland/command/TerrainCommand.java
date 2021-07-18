package com.hikarishima.lightland.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hikarishima.lightland.config.FileIO;
import com.lcy0x1.core.util.ExceptionHandler;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TerrainCommand {

    public static void register(LiteralArgumentBuilder<CommandSource> lightland) {
        lightland.then(Commands.literal("terrain")
                .then(Commands.literal("export")
                        .requires((s) -> s.hasPermission(2))
                        .executes((context) -> {
                            ServerWorld w = context.getSource().getLevel();
                            BufferedImage img = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_RGB);
                            JsonArray arr = new JsonArray();
                            Map<Biome, Integer> map = new HashMap<>();
                            for (int i = 0; i < 1024; i++)
                                for (int j = 0; j < 1024; j++) {
                                    Biome b = w.getNoiseBiome(i, 0, j);
                                    int color;
                                    if (map.containsKey(b))
                                        color = map.get(b);
                                    else {
                                        map.put(b, color = new Random().nextInt(0xFFFFFF));
                                        JsonObject obj = new JsonObject();
                                        obj.addProperty("color", Integer.toString(color, 16));
                                        obj.addProperty("id", b.getRegistryName().toString());
                                        arr.add(obj);
                                    }
                                    img.setRGB(i, j, color);
                                }
                            File file_img = FileIO.getFile("gen_biome.png");
                            File file_json = FileIO.getFile("gen_biome.json");
                            FileIO.checkFile(file_img);
                            FileIO.checkFile(file_json);
                            ExceptionHandler.run(()->{
                                ImageIO.write(img,"PNG",file_img);
                                PrintStream stream = new PrintStream(file_json);
                                stream.println(arr);
                                stream.close();
                            });
                            return 1;
                        })));
    }

}
