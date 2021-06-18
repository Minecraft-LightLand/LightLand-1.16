package com.hikarishima.lightland.mobspawn;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.IWorld;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileReader;
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

    public static void init() {
        IMobLevel.readFile(EquipLevel.EquipItem.class, EquipLevel.ITEMS, "item_cost.json");
        IMobLevel.readFile(EquipLevel.Enchant.class, EquipLevel.ENCHANTS, "enchant_cost.json");
        IMobLevel.readFile(BuffLevel.Buff.class, BuffLevel.LIST, "buff_cost.json");
        IMobLevel.readFile(PotionLevel.PotionEntry.class, PotionLevel.LIST, "potion_cost.json");
    }


    static <T> void readFile(Class<T> cls, List<T> list, String name) {
        list.clear();
        String path = FMLPaths.CONFIGDIR.get().toString();
        File file = new File(path + File.separator + "lightland" + File.separator + name);
        if (file.exists()) {
            JsonElement elem = ExceptionHandler.get(() -> new JsonParser().parse(new FileReader(file)));
            if (elem != null && elem.isJsonArray()) {
                for (JsonElement e : elem.getAsJsonArray()) {
                    list.add(Serializer.from(e.getAsJsonObject(), cls, null));
                }
            }
        } else {
            LogManager.getLogger().warn(file.toString() + " does not exist");
        }
    }

    static interface Entry<T> {

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
            money -= sele.getCost();
            result.add(sele);
            T select = sele;
            supply.removeIf(e -> select.equal(e));
            double max = money;
            supply.removeIf(e -> e.getCost() > max);
        }
        return result;
    }

}
