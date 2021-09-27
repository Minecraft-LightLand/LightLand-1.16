package com.hikarishima.lightland.magic.registry;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.registry.effect.*;
import com.hikarishima.lightland.magic.registry.enchantment.HeavyEnchantment;
import com.hikarishima.lightland.magic.registry.enchantment.PhysicsProtectionEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class VanillaMagicRegistry {

    public static final DeferredRegister<Effect> EFFECT = DeferredRegister.create(Effect.class, LightLandMagic.MODID);
    public static final DeferredRegister<Enchantment> ENCH = DeferredRegister.create(Enchantment.class, LightLandMagic.MODID);
    public static final DeferredRegister<Potion> POTION = DeferredRegister.create(Potion.class, LightLandMagic.MODID);

    public static final RegistryObject<Effect> EFF_ARCANE = EFFECT.register("arcane", ArcaneEffect::new);
    public static final RegistryObject<Effect> EFF_DROWN = EFFECT.register("drown", DrownEffect::new);
    public static final RegistryObject<Effect> EFF_WATER_TRAP = EFFECT.register("water_trap", WaterTrapEffect::new);
    public static final RegistryObject<Effect> EFF_HEAVY = EFFECT.register("heavy", HeavyEffect::new);
    public static final RegistryObject<Effect> EFF_PETRI = EFFECT.register("petrification", PetrificationEffect::new);
    public static final RegistryObject<Effect> EFF_EMERALD = EFFECT.register("emerald_pope", EmeraldPopeEffect::new);
    public static final RegistryObject<Effect> EFF_ATTRACT = EFFECT.register("attract", AttractEffect::new);
    public static final RegistryObject<Effect> EFF_ATTRACTED = EFFECT.register("attracted", AttractedEffect::new);

    public static final RegistryObject<Potion> POTION_SAT = POTION.register("saturation", () -> new Potion(new EffectInstance(Effects.SATURATION, 20, 0)));
    public static final RegistryObject<Potion> POTION_EM0 = POTION.register("emerald_0", () -> new Potion(new EffectInstance(EFF_EMERALD.get(), 20, 0)));
    public static final RegistryObject<Potion> POTION_EM1 = POTION.register("emerald_1", () -> new Potion(new EffectInstance(EFF_EMERALD.get(), 20, 1)));

    public static final RegistryObject<Enchantment> ENCH_HEAVY = ENCH.register("heavy", HeavyEnchantment::new);
    public static final RegistryObject<Enchantment> ENCH_PHYSICS = ENCH.register("physics_protection", PhysicsProtectionEnchantment::new);

}
