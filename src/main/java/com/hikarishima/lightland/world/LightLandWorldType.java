package com.hikarishima.lightland.world;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraftforge.common.world.ForgeWorldType;

public class LightLandWorldType extends ForgeWorldType {

    public LightLandWorldType() {
        super(LightLandWorldType::getCG);
    }

    public static ChunkGenerator getCG(Registry<Biome> regBiome, Registry<DimensionSettings> regDS, long seed) {
        //TODO remove features
        return new NoiseChunkGenerator(new LightLandBiomeProvider(seed, regBiome), seed, () -> regDS.getOrThrow(DimensionSettings.OVERWORLD));
    }

}
