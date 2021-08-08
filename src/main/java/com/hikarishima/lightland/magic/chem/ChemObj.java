package com.hikarishima.lightland.magic.chem;

import com.lcy0x1.core.chem.AbChemObj;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

@SerialClass
public abstract class ChemObj<T extends IForgeRegistryEntry<T>> extends AbChemObj {

    ChemObj(State def) {
        state = def;
    }

    public abstract T get();

    @SerialClass.SerialField
    public ResourceLocation id;


}
