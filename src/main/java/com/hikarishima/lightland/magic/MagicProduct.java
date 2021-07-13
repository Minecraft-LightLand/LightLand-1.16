package com.hikarishima.lightland.magic;

import com.lcy0x1.core.magic.HexHandler;
import com.lcy0x1.core.util.NBTObj;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class MagicProduct<I extends IForgeRegistryEntry<I>, P extends MagicProduct<I, P>> {

    public final MagicProductType<I, P> type;
    public final ResourceLocation rl;
    public final I item;
    public final NBTObj tag;
    private HexHandler best;

    public MagicProduct(MagicProductType<I, P> type, NBTObj tag, ResourceLocation rl) {
        this.type = type;
        this.tag = tag;
        this.rl = rl;
        this.item = type.getter.apply(rl);
        if (tag.tag.contains("hex"))
            best = new HexHandler(tag.getSub("hex"));
    }

}
