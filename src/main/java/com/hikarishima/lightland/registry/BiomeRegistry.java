package com.hikarishima.lightland.registry;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.world.LavaSurfaceBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.surfacebuilders.*;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeRegistry {

    public static final BlockState BS_LAVA = Blocks.LAVA.defaultBlockState();
    public static final BlockState BS_BASE = Blocks.BASALT.defaultBlockState();
    public static final BlockState BS_MAGMA = Blocks.MAGMA_BLOCK.defaultBlockState();
    public static final SurfaceBuilderConfig SBC_LAVA = new SurfaceBuilderConfig(BS_LAVA, BS_BASE, BS_MAGMA);

    public static final SurfaceBuilder<SurfaceBuilderConfig> SB_LAVA = reg("volcano_lava", new LavaSurfaceBuilder(SurfaceBuilderConfig.CODEC));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> CSB_LAVA = reg("volcano_lava",SB_LAVA.configured(SBC_LAVA));

    public static final Biome VOLCANO = reg("volcano", genBadlandsBiome(3f, 0.1f));
    public static final Biome VOLCANO_LAVA = reg("volcano_lava", genVolcanoLavaBiome(3f, 0));
    public static final Biome VOLCANO_SIDE = reg("volcano_side", genBadlandsBiome(2f, 0.3f));

    private static <V extends T, T extends ForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(LightLand.MODID, name);
        return v;
    }
    private static <SC extends ISurfaceBuilderConfig> ConfiguredSurfaceBuilder<SC> reg(String name, ConfiguredSurfaceBuilder<SC> csb) {
        return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_SURFACE_BUILDER, LightLand.MODID+":"+name, csb);
    }

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

    private static Biome genVolcanoLavaBiome(float depth, float scale) {
        BiomeGenerationSettings.Builder bgs = new BiomeGenerationSettings.Builder().surfaceBuilder(CSB_LAVA);
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
