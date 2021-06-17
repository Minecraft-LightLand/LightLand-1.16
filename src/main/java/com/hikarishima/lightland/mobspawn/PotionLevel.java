package com.hikarishima.lightland.mobspawn;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class PotionLevel {

    public static final List<PotionEntry> LIST = new ArrayList<>();

    public static void init() {
        LIST.clear();
        String path = FMLPaths.CONFIGDIR.get().toString();
        File file = new File(path + File.separator + "lightland" + File.separator + "potion_cost.json");
        if (file.exists()) {
            JsonElement elem = ExceptionHandler.get(() -> new JsonParser().parse(new FileReader(file)));
            if (elem != null && elem.isJsonArray()) {
                for (JsonElement e : elem.getAsJsonArray()) {
                    LIST.add(Serializer.from(e.getAsJsonObject(), PotionEntry.class, new PotionEntry()));
                }
            }
        } else {
            LogManager.getLogger().warn(file.toString() + " does not exist");
        }
    }

    @SerialClass
    public static class PotionEntry {

        @SerialClass.SerialField
        public String effect;

        @SerialClass.SerialField
        public int max, weight;

        @SerialClass.SerialField
        public double cost;

        public Effect eff;

        @SerialClass.OnInject
        public void onInject() {
            eff = ExceptionHandler.get(() -> ForgeRegistries.POTIONS.getValue(ResourceLocation.tryParse(effect)));
        }

    }

    public static class PotionIns {

        public PotionEntry buff;
        public int lv;

        public PotionIns(PotionEntry buff, int lv) {
            this.buff = buff;
            this.lv = lv;
        }

    }

    public static double modify(IWorld world, LivingEntity ent, double difficulty) {
        List<PotionIns> list = new ArrayList<>();
        for (PotionEntry entry : LIST) {
            for (int i = 1; i <= entry.max; i++)
                if (entry.cost * i < difficulty)
                    list.add(new PotionIns(entry, i));
        }
        if (list.size() == 0)
            return 0;
        List<PotionIns> insList = new ArrayList<>();
        while (list.size() > 0) {
            int total_weight = 0;
            for (PotionIns buff : list) {
                total_weight += buff.buff.weight * buff.lv;
            }
            int rand = world.getRandom().nextInt(total_weight);
            PotionIns sele = null;
            for (PotionIns buff : list) {
                sele = buff;
                if (rand < buff.buff.weight * buff.lv)
                    break;
                rand -= buff.buff.weight * buff.lv;
            }
            difficulty -= sele.buff.cost * sele.lv;
            insList.add(sele);
            PotionIns select = sele;
            list.removeIf(e -> e.buff.eff == select.buff.eff);
        }
        int cost = 0;
        for (PotionIns ins : insList) {
            ent.addEffect(new EffectInstance(ins.buff.eff, 12000,ins.lv - 1));
            cost += ins.buff.cost * ins.lv;
        }
        return cost;
    }

}
