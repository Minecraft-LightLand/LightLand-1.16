package com.hikarishima.lightland.mobspawn;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BuffLevel {

    public static final List<Buff> LIST = new ArrayList<>();
    public static final UUID MODIFIER_UUID = new UUID(453447673, 432547674);
    public static final String NAME = "buff_leveler";

    public static void init() {
        LIST.clear();
        String path = FMLPaths.CONFIGDIR.get().toString();
        File file = new File(path + File.separator + "lightland" + File.separator + "buff_cost.json");
        if (file.exists()) {
            JsonElement elem = ExceptionHandler.get(() -> new JsonParser().parse(new FileReader(file)));
            if (elem != null && elem.isJsonArray()) {
                for (JsonElement e : elem.getAsJsonArray()) {
                    LIST.add(Serializer.from(e.getAsJsonObject(), Buff.class, new Buff()));
                }
            }
        } else {
            LogManager.getLogger().warn(file.toString() + " does not exist");
        }
    }

    @SerialClass
    public static class Buff {

        @SerialClass.SerialField
        public String attribute;

        @SerialClass.SerialField
        public int max, weight;

        @SerialClass.SerialField
        public double cost, base;

        public Attribute attr;

        @SerialClass.OnInject
        public void onInject() {
            attr = ExceptionHandler.get(() -> ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.tryParse(attribute)));
        }

    }

    public static class BuffIns {

        public Buff buff;
        public int lv;

        public BuffIns(Buff buff, int lv) {
            this.buff = buff;
            this.lv = lv;
        }

    }

    public static double modify(IWorld world, LivingEntity ent, double difficulty) {
        AttributeModifierManager attrs = ent.getAttributes();
        List<BuffIns> list = new ArrayList<>();
        for (Buff buff : LIST) {
            if (!attrs.hasAttribute(buff.attr))
                continue;
            for (int i = 1; i <= buff.max; i++) {
                if (buff.cost * i < difficulty)
                    list.add(new BuffIns(buff, i));
            }
        }
        if (list.size() == 0)
            return 0;
        List<BuffIns> insList = new ArrayList<>();
        while (list.size() > 0) {
            int total_weight = 0;
            for (BuffIns buff : list) {
                total_weight += buff.buff.weight * buff.lv;
            }
            int rand = world.getRandom().nextInt(total_weight);
            BuffIns sele = null;
            for (BuffIns buff : list) {
                sele = buff;
                if (rand < buff.buff.weight * buff.lv)
                    break;
                rand -= buff.buff.weight * buff.lv;
            }
            difficulty -= sele.buff.cost * sele.lv;
            insList.add(sele);
            BuffIns select = sele;
            list.removeIf(e -> e.buff.attr == select.buff.attr);
        }
        int cost = 0;
        for (BuffIns ins : insList) {
           ModifiableAttributeInstance mains = attrs.getInstance(ins.buff.attr);
            if(mains!=null){
                double coef = Math.pow(ins.buff.base, ins.lv);
                AttributeModifier mod = new AttributeModifier(MODIFIER_UUID, NAME, coef, AttributeModifier.Operation.MULTIPLY_BASE);
                mains.addPermanentModifier(mod);
                cost += ins.buff.cost * ins.lv;
            }
        }
        return cost;
    }

}
