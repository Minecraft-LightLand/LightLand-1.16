package com.hikarishima.lightland.magic.registry;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.registry.effect.ArcaneEffect;
import com.hikarishima.lightland.magic.registry.effect.DrownEffect;
import com.hikarishima.lightland.magic.registry.effect.HeavyEffect;
import com.hikarishima.lightland.magic.registry.effect.WaterTrapEffect;
import com.hikarishima.lightland.magic.registry.enchantment.HeavyEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Effect;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class VanillaMagicRegistry {

    public static final Effect EFF_ARCANE = reg("arcane", new ArcaneEffect());
    public static final Effect EFF_DROWN = reg("drown", new DrownEffect());
    public static final Effect EFF_WATER_TRAP = reg("water_trap", new WaterTrapEffect());
    public static final Effect EFF_HEAVY = reg("heavy", new HeavyEffect());

    public static final Enchantment ENCH_HEAVY = reg("heavy", new HeavyEnchantment());

    private static <V extends T, T extends ForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(LightLandMagic.MODID, name);
        return v;
    }

}
