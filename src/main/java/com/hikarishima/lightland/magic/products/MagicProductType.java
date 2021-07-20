package com.hikarishima.lightland.magic.products;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.lcy0x1.core.util.NBTObj;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Function;

public class MagicProductType<I extends IForgeRegistryEntry<I>, P extends MagicProduct<I, P>> extends MagicRegistry.MPTRaw {

    public final Class<P> cls;
    public final MagicFactory<I, P> fac;
    public final Function<ResourceLocation, I> getter;
    public final Function<I, String> namer;
    public final I icon;

    public MagicProductType(Class<P> cls, MagicFactory<I, P> fac,
                            Function<ResourceLocation, I> getter, Function<I, String> namer, I icon) {
        this.cls = cls;
        this.fac = fac;
        this.getter = getter;
        this.namer = namer;
        this.icon = icon;
    }

    @FunctionalInterface
    public interface MagicFactory<I extends IForgeRegistryEntry<I>, P extends MagicProduct<I, P>> {

        P get(MagicHandler player, NBTObj nbtManager, ResourceLocation rl, IMagicRecipe<?> r);

    }

}
