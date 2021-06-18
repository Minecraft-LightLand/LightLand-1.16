package com.hikarishima.lightland.mobspawn;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.IWorld;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public interface IMobLevel {

    double POTION_PART = 0.33, FAIL_PARTIAL = 0.2;

    static boolean apply(IWorld world, LivingEntity ent, double difficulty) {
        double def = difficulty;
        difficulty -= EquipLevel.modify(world, ent, difficulty);
        difficulty -= PotionLevel.modify(world, ent, difficulty * POTION_PART);
        difficulty -= BuffLevel.modify(world, ent, difficulty);
        return true;
    }

    static <T> void readFile(Class<T> cls, List<T> list, String name) {
        list.clear();
        String path = FMLPaths.CONFIGDIR.get().toString();
        File file = new File(path + File.separator + "lightland" + File.separator + name);
        if (!file.exists()) {
            ExceptionHandler.run(() -> {
                String jar_path = "/data/lightland/default_config/" + name;
                InputStream is = IMobLevel.class.getResourceAsStream(jar_path);
                if (is != null)
                    Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            });
        }
        ExceptionHandler.run(() -> {
            JsonElement elem = new JsonParser().parse(new FileReader(file));
            if (elem != null && elem.isJsonArray()) {
                for (JsonElement e : elem.getAsJsonArray()) {
                    list.add(Serializer.from(e.getAsJsonObject(), cls, null));
                }
            }
        });
    }

    interface Entry<T> {

        int getWeight();

        double getCost();

        boolean equal(T other);

        double getChance();
    }

    static <T extends Entry> List<T> loot(IWorld world, List<T> supply, double money) {
        List<T> result = new ArrayList<>();
        while (supply.size() > 0) {
            int total_weight = 0;
            for (T buff : supply) {
                total_weight += buff.getWeight();
            }
            int rand = world.getRandom().nextInt(total_weight);
            T sele = null;
            for (T entry : supply) {
                sele = entry;
                if (rand < entry.getWeight())
                    break;
                rand -= entry.getWeight();
            }
            if (sele.getChance() >= world.getRandom().nextDouble()) {
                money -= sele.getCost();
                result.add(sele);
                T select = sele;
                supply.removeIf(e -> select.equal(e));
                double max = money;
                supply.removeIf(e -> e.getCost() > max);
            } else supply.remove(sele);
        }
        return result;
    }

}
