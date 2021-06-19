package com.hikarishima.lightland.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.layer.Layer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.stream.Collectors;

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
        Biome biome = ImageBiomeReader.getBiome(x, z);
        if (biome != null)
            return biome;
        return biomes.get(Biomes.DEEP_OCEAN);
    }
}
