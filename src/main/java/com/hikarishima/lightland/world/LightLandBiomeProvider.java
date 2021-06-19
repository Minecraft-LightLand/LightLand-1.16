package com.hikarishima.lightland.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

public class LightLandBiomeProvider extends BiomeProvider {

    public static final Codec<LightLandBiomeProvider> CODEC = RecordCodecBuilder.create((builder) ->
            builder.group(
                    Codec.LONG.fieldOf("seed").stable().forGetter((bp) -> bp.seed),
                    RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter((bp) -> bp.biomes)
            ).apply(builder, builder.stable(LightLandBiomeProvider::new)));

    private final long seed;
    private final Registry<Biome> biomes;

    protected LightLandBiomeProvider(long seed, Registry<Biome> biomes) {
        super(new ArrayList<>(ForgeRegistries.BIOMES.getValues()));
        this.seed = seed;
        this.biomes = biomes;
    }

    @Override
    protected Codec<? extends BiomeProvider> codec() {
        return CODEC;
    }

    @Override
    public BiomeProvider withSeed(long l) {
        return new LightLandBiomeProvider(seed, biomes);
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        ResourceLocation rl = ImageBiomeReader.getBiome(x, z);
        if (rl == null)
            return biomes.get(Biomes.DEEP_OCEAN);
        Biome biome = biomes.get(rl);
        if (biome == null)
            return biomes.get(Biomes.DEEP_OCEAN);
        return biome;

    }
}
