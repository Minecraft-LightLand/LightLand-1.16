package com.hikarishima.lightland.magic.registry;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.registry.effect.*;
import com.hikarishima.lightland.magic.registry.enchantment.HeavyEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class VanillaMagicRegistry {

    public static final DeferredRegister<Effect> EFFECT = DeferredRegister.create(Effect.class, LightLandMagic.MODID);
    public static final DeferredRegister<Enchantment> ENCH = DeferredRegister.create(Enchantment.class, LightLandMagic.MODID);

    public static final RegistryObject<Effect> EFF_ARCANE = EFFECT.register("arcane", ArcaneEffect::new);
    public static final RegistryObject<Effect> EFF_DROWN = EFFECT.register("drown", DrownEffect::new);
    public static final RegistryObject<Effect> EFF_WATER_TRAP = EFFECT.register("water_trap", WaterTrapEffect::new);
    public static final RegistryObject<Effect> EFF_HEAVY = EFFECT.register("heavy", HeavyEffect::new);
    public static final RegistryObject<Effect> EFF_PETRI = EFFECT.register("petrification", PetrificationEffect::new);

    public static final RegistryObject<Enchantment> ENCH_HEAVY = ENCH.register("heavy", HeavyEnchantment::new);

}
