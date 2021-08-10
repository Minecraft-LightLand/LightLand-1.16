package com.hikarishima.lightland.magic.chem;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.lcy0x1.core.chem.AbChemObj;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;

@SerialClass
public abstract class ChemObj<O extends ChemObj<O, T>, T extends IForgeRegistryEntry<T>> extends AbChemObj {

    /**
     * return null if the object is unknown to player
     */
    @OnlyIn(Dist.CLIENT)
    public static ChemObj<?, ?> cast(MagicHandler handler, AbChemObj obj) {
        if (!(obj instanceof ChemObj))
            return null;
        ChemObj<?, ?> ans = (ChemObj<?, ?>) obj;
        if (ans.known(handler))
            return ans;
        return null;
    }

    ChemObj(State def) {
        state = def;
    }

    public abstract T get();

    public abstract boolean known(MagicHandler handler);

    public abstract IFormattableTextComponent getDesc();

    @SerialClass.SerialField
    public ResourceLocation id;


}
