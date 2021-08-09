package com.hikarishima.lightland.magic.chem;

import com.google.common.collect.Maps;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.chem.AbChemObj;
import com.lcy0x1.core.chem.EquationPool;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
@SerialClass
public class HashEquationPool extends EquationPool {

    public static HashEquationPool getPool(World world) {
        return ConfigRecipe.getObject(world, ConfigRecipe.CHEM, "pool");
    }

    @SuppressWarnings("unchecked")
    public static <O extends ChemObj<O, I>, I extends IForgeRegistryEntry<I>> O getChemObj(World world, I item) {
        HashEquationPool pool = getPool(world);
        AbChemObj obj = pool.objects.get(pool.cache.get(Objects.requireNonNull(item.getRegistryName()).toString()));
        if (!(obj instanceof ChemObj))
            return null;
        return (O) obj;
    }

    public final Map<String, String> cache = Maps.newLinkedHashMap();

    @SerialClass.OnInject
    public void onInject() {
        super.onInject();
        objects.forEach((k, v) -> {
            if (v instanceof ChemObj<?, ?>)
                cache.put(Objects.requireNonNull(((ChemObj<?, ?>) v).get().getRegistryName()).toString(), k);
        });
    }

}
