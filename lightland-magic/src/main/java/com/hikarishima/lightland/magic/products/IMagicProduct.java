package com.hikarishima.lightland.magic.products;

import com.hikarishima.lightland.magic.MagicRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class IMagicProduct<I extends IForgeRegistryEntry<I>, P extends MagicProduct<I, P>> {

    public final MagicProductType<I, P> type;
    public final ResourceLocation rl;
    public final I item;

    public IMagicProduct(MagicProductType<I, P> type, ResourceLocation rl) {
        this.type = type;
        this.rl = rl;
        this.item = type.getter.apply(rl);
    }

    public String getDescriptionID() {
        return type.namer.apply(item);
    }

    public static IMagicProduct<?, ?> getInstance(MagicProductType<?,?> type, ResourceLocation rl) {
        return type.fac.get(null, null, rl, null);
    }

}
