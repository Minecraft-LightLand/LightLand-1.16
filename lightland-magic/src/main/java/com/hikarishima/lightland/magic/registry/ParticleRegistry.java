package com.hikarishima.lightland.magic.registry;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.registry.particle.EmeraldParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleRegistry {

    public static final DeferredRegister<ParticleType<?>> PARTICLE = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, LightLandMagic.MODID);

    public static final RegistryObject<BasicParticleType> EMERALD = PARTICLE.register("emerald", () -> new BasicParticleType(false));

    @OnlyIn(Dist.CLIENT)
    public static void register() {
        Minecraft.getInstance().particleEngine.register(EMERALD.get(), EmeraldParticle.Factory::new);
    }
}
