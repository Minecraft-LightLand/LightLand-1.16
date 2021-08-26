package com.hikarishima.lightland.magic.products;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.info.TypeConfig;
import com.hikarishima.lightland.magic.recipe.IMagicRecipe;
import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.proxy.Proxy;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.util.NBTObj;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Function;
import java.util.function.Supplier;

public class MagicProductType<I extends IForgeRegistryEntry<I>, P extends MagicProduct<I, P>> extends MagicRegistry.MPTRaw {

    public final Class<P> cls;
    public final MagicFactory<I, P> fac;
    public final Function<ResourceLocation, I> getter;
    public final Function<I, String> namer;
    public final Supplier<IForgeRegistry<I>> registry;
    public final MagicElement elem;

    public MagicProductType(Class<P> cls, MagicFactory<I, P> fac, Supplier<IForgeRegistry<I>> registry,
                            Function<I, String> namer, MagicElement elem) {
        this.cls = cls;
        this.fac = fac;
        this.getter = (s) -> registry.get().getValue(s);
        this.namer = namer;
        this.registry = registry;
        this.elem = elem;
    }

    public TypeConfig getDisplay() {
        return ConfigRecipe.getObject(Proxy.getPlayer().level, MagicRecipeRegistry.PRODUCT_TYPE_DISPLAY, getID());
    }

    @FunctionalInterface
    public interface MagicFactory<I extends IForgeRegistryEntry<I>, P extends MagicProduct<I, P>> {

        P get(MagicHandler player, NBTObj nbtManager, ResourceLocation rl, IMagicRecipe<?> r);

    }

}
