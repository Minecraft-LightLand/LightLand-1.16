package com.hikarishima.lightland.magic.registry;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.registry.effect.ArcaneEffect;
import com.hikarishima.lightland.magic.registry.effect.DrownEffect;
import com.hikarishima.lightland.magic.registry.effect.WaterTrapEffect;
import net.minecraft.potion.Effect;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class VanillaMagicRegistry {

    public static final Effect ARCANE = reg("arcane", new ArcaneEffect());
    public static final Effect DROWN = reg("drown", new DrownEffect());
    public static final Effect WATER_TRAP = reg("water_trap", new WaterTrapEffect());

    private static <V extends T, T extends ForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(LightLandMagic.MODID, name);
        return v;
    }

}
