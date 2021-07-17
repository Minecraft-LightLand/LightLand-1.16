package com.hikarishima.lightland.mobspawn;

import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BuffLevel {

    public static final List<Buff> LIST = new ArrayList<>();
    public static final UUID MODIFIER_UUID = new UUID(453447673, 432547674);
    public static final String NAME = "buff_leveler";

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
        List<BuffIns> insList = IMobLevel.loot(world, list, difficulty);
        int cost = 0;
        for (BuffIns ins : insList) {
            ModifiableAttributeInstance mains = attrs.getInstance(ins.buff.attr);
            if (mains != null) {
                double coef = Math.pow(ins.buff.base, ins.lv);
                AttributeModifier mod = new AttributeModifier(MODIFIER_UUID, NAME, coef, ins.buff.operation);
                mains.addPermanentModifier(mod);
                cost += ins.buff.cost * ins.lv;
            }
        }
        return cost;
    }

    @SerialClass
    public static class Buff {

        @SerialClass.SerialField
        public String id;

        @SerialClass.SerialField
        public int max, weight;

        @SerialClass.SerialField
        public double cost, base, chance;

        @SerialClass.SerialField
        public AttributeModifier.Operation operation = AttributeModifier.Operation.MULTIPLY_TOTAL;

        public Attribute attr;

        @SerialClass.OnInject
        public void onInject() {
            attr = ExceptionHandler.get(() -> ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.tryParse(id)));
        }

    }

    public static class BuffIns implements IMobLevel.Entry<BuffIns> {

        public Buff buff;
        public int lv;

        public BuffIns(Buff buff, int lv) {
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
        public boolean equal(BuffIns other) {
            return buff.attr == other.buff.attr;
        }

        @Override
        public double getChance() {
            return buff.chance;
        }
    }

}
