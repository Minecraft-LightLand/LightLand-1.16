package com.hikarishima.lightland.registry;

import com.hikarishima.lightland.LightLand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;

public class BiomeRegistry {

    public static final Biome VOLCANO = genBadlandsBiome(3f, 0.1f)
            .setRegistryName(LightLand.MODID, "volcano");

    public static final Biome VOLCANO_SIDE = genBadlandsBiome(2f, 0.3f)
            .setRegistryName(LightLand.MODID, "volcano_side");

    public static Biome genMountainBiome(float depth, float scale, float temp, float fall) {
        MobSpawnInfo.Builder builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.farmAnimals(builder);
        BiomeGenerationSettings.Builder bgs = new BiomeGenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.MOUNTAIN);
        DefaultBiomeFeatures.addDefaultSoftDisks(bgs);
        DefaultBiomeFeatures.addMountainTrees(bgs);
        DefaultBiomeFeatures.addDefaultFlowers(bgs);
        DefaultBiomeFeatures.addDefaultGrass(bgs);
        DefaultBiomeFeatures.addDefaultMushrooms(bgs);
        DefaultBiomeFeatures.addDefaultExtraVegetation(bgs);
        DefaultBiomeFeatures.addDefaultSprings(bgs);
        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.EXTREME_HILLS)
                .depth(depth).scale(scale).temperature(temp).downfall(fall)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(4159204)
                        .waterFogColor(329011)
                        .fogColor(12638463)
                        .skyColor(calculateSkyColor(0.2F))
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
                        .build()).mobSpawnSettings(builder.build())
                .generationSettings(bgs.build()).build();

    }

    private static Biome genBadlandsBiome(float depth, float scale) {
        BiomeGenerationSettings.Builder bgs = new BiomeGenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.BADLANDS);
        DefaultBiomeFeatures.addBadlandGrass(bgs);
        DefaultBiomeFeatures.addBadlandExtraVegetation(bgs);
        DefaultBiomeFeatures.addDefaultSprings(bgs);
        return (new net.minecraft.world.biome.Biome.Builder())
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.MESA)
                .depth(depth).scale(scale).temperature(2.0F).downfall(0.0F)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(4159204).waterFogColor(329011).fogColor(12638463)
                        .skyColor(calculateSkyColor(2.0F))
                        .foliageColorOverride(10387789)
                        .grassColorOverride(9470285)
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS).build())
                .mobSpawnSettings(new MobSpawnInfo.Builder().build())
                .generationSettings(bgs.build()).build();
    }

    private static int calculateSkyColor(float col) {
        float c = col / 3.0F;
        c = MathHelper.clamp(c, -1.0F, 1.0F);
        return MathHelper.hsvToRgb(0.62222224F - c * 0.05F, 0.5F + c * 0.1F, 1.0F);
    }


}
