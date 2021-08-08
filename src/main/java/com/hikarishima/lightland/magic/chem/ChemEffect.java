package com.hikarishima.lightland.magic.chem;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.potion.Effect;
import net.minecraftforge.registries.ForgeRegistries;

@SerialClass
public class ChemEffect extends ChemObj<Effect> {

    @SerialClass.SerialField
    public int lv;

    public ChemEffect() {
        super(State.LIQUID);
    }

    @Override
    public Effect get() {
        return ForgeRegistries.POTIONS.getValue(id);
    }
}
