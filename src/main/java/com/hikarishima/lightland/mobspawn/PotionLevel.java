package com.hikarishima.lightland.mobspawn;

import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

public class PotionLevel {

    public static final List<PotionEntry> LIST = new ArrayList<>();

    public static double modify(IWorld world, LivingEntity ent, double difficulty) {
        List<PotionIns> list = new ArrayList<>();
        for (PotionEntry entry : LIST) {
            for (int i = 1; i <= entry.max; i++)
                if (entry.cost * i < difficulty)
                    list.add(new PotionIns(entry, i));
        }
        if (list.size() == 0)
            return 0;
        List<PotionIns> insList = IMobLevel.loot(world, list, difficulty);
        int cost = 0;
        for (PotionIns ins : insList) {
            ent.addEffect(new EffectInstance(ins.buff.eff, 12000, ins.lv - 1));
            cost += ins.buff.cost * ins.lv;
        }
        return cost;
    }

    @SerialClass
    public static class PotionEntry {

        @SerialClass.SerialField
        public String id;

        @SerialClass.SerialField
        public int max, weight;

        @SerialClass.SerialField
        public double cost, chance;

        public Effect eff;

        @SerialClass.OnInject
        public void onInject() {
            eff = ExceptionHandler.get(() -> ForgeRegistries.POTIONS.getValue(ResourceLocation.tryParse(id)));
            if (eff == null) {
                LogManager.getLogger().error("potion status effect " + id + " does not exist");
            }
        }

    }

    public static class PotionIns implements IMobLevel.Entry<PotionIns> {

        public PotionEntry buff;
        public int lv;

        public PotionIns(PotionEntry buff, int lv) {
            this.buff = buff;
            this.lv = lv;
        }

        @Override
        public int getWeight() {
            return buff.weight * lv;
        }

        @Override
        public double getCost() {
            return buff.cost * lv;
        }

        @Override
        public boolean equal(PotionIns other) {
            return buff.eff == other.buff.eff;
        }

        @Override
        public double getChance() {
            return buff.chance;
        }
    }

}
