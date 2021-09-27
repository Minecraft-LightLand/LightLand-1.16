package com.hikarishima.lightland.magic.registry;

import com.google.common.collect.Lists;
import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.registry.particle.EmeraldParticle;
import com.hikarishima.lightland.magic.registry.particle.ToolParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class ParticleRegistry {

    public static final DeferredRegister<ParticleType<?>> PARTICLE = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, LightLandMagic.MODID);

    public static final RegistryObject<BasicParticleType> EMERALD = PARTICLE.register("emerald", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> PICKAXE_0 = PARTICLE.register("wooden_pickaxe", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> PICKAXE_1 = PARTICLE.register("stone_pickaxe", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> PICKAXE_2 = PARTICLE.register("iron_pickaxe", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> PICKAXE_3 = PARTICLE.register("diamond_pickaxe", () -> new BasicParticleType(false));

    public static final List<RegistryObject<BasicParticleType>> PICKAXE = Lists.newArrayList(PICKAXE_0, PICKAXE_1, PICKAXE_2, PICKAXE_3);

    @OnlyIn(Dist.CLIENT)
    public static void register() {
        Minecraft.getInstance().particleEngine.register(EMERALD.get(), EmeraldParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(PICKAXE_0.get(), ToolParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(PICKAXE_1.get(), ToolParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(PICKAXE_2.get(), ToolParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(PICKAXE_3.get(), ToolParticle.Factory::new);
    }
}
