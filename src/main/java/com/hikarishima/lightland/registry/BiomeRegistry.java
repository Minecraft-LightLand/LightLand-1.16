package com.hikarishima.lightland.registry;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.world.LavaSurfaceBuilder;
import com.hikarishima.lightland.world.MagmaSurfaceBuilder;
import com.hikarishima.lightland.world.TerracotaSurfaceBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.surfacebuilders.*;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeRegistry {

    public static final BlockState BS_DEF = Blocks.TERRACOTTA.defaultBlockState();
    public static final BlockState BS_BASE = Blocks.BASALT.defaultBlockState();
    public static final BlockState BS_MAGMA = Blocks.MAGMA_BLOCK.defaultBlockState();
    public static final SurfaceBuilderConfig SBC_LAVA = new SurfaceBuilderConfig(BS_DEF, BS_BASE, BS_MAGMA);

    public static final SurfaceBuilder<SurfaceBuilderConfig> SB_LAVA = reg("lava_lake", new LavaSurfaceBuilder(SurfaceBuilderConfig.CODEC));
    public static final SurfaceBuilder<SurfaceBuilderConfig> SB_BEACH = reg("lava_beach", new MagmaSurfaceBuilder(SurfaceBuilderConfig.CODEC));
    public static final SurfaceBuilder<SurfaceBuilderConfig> SB_TERRA = reg("volcano_top", new TerracotaSurfaceBuilder(SurfaceBuilderConfig.CODEC));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> CSB_LAVA = reg("lava_lake", SB_LAVA.configured(SBC_LAVA));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> CSB_BEACH = reg("lava_beach", SB_BEACH.configured(SBC_LAVA));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> CSB_TERRA = reg("volcano_top", SB_TERRA.configured(SBC_LAVA));

    public static final Biome VOLCANO_LAVA = reg("lava_lake", genVolcanoLavaBiome(4f, 0f, CSB_LAVA));
    public static final Biome VOLCANO_BEACH = reg("lava_beach", genVolcanoLavaBiome(5f, 0f, CSB_BEACH));
    public static final Biome VOLCANO_TOP = reg("volcano_top", genVolcanoLavaBiome(5f, 0f,CSB_TERRA));
    public static final Biome VOLCANO_SIDE_0 = reg("volcano_side_0", genBadlandsBiome(4.5f, 0.025f));
    public static final Biome VOLCANO_SIDE_1 = reg("volcano_side_1", genBadlandsBiome(4.0f, 0.025f));
    public static final Biome VOLCANO_SIDE_2 = reg("volcano_side_2", genBadlandsBiome(3.5f, 0.025f));
    public static final Biome VOLCANO_SIDE_3 = reg("volcano_side_3", genBadlandsBiome(3.0f, 0.025f));
    public static final Biome VOLCANO_SIDE_4 = reg("volcano_side_4", genBadlandsBiome(2.5f, 0.025f));
    public static final Biome VOLCANO_SIDE_5 = reg("volcano_side_5", genBadlandsBiome(2.0f, 0.025f));
    public static final Biome VOLCANO_SIDE_6 = reg("volcano_side_6", genBadlandsBiome(1.5f, 0.025f));
    public static final Biome VOLCANO_SIDE_7 = reg("volcano_side_7", genBadlandsBiome(1.0f, 0.025f));

    public static boolean isLavaLakeBiome(Biome b) {
        return b.getRegistryName() != null &&
                (b.getRegistryName().equals(VOLCANO_LAVA.getRegistryName()) ||
                        b.getRegistryName().equals(VOLCANO_BEACH.getRegistryName()) ||
                        b.getRegistryName().equals(VOLCANO_TOP.getRegistryName()));
    }

    private static <V extends T, T extends ForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(LightLand.MODID, name);
        return v;
    }

    private static <SC extends ISurfaceBuilderConfig> ConfiguredSurfaceBuilder<SC> reg(String name, ConfiguredSurfaceBuilder<SC> csb) {
        return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_SURFACE_BUILDER, LightLand.MODID + ":" + name, csb);
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

    private static Biome genVolcanoLavaBiome(float depth, float scale,ConfiguredSurfaceBuilder<SurfaceBuilderConfig> csb) {
        BiomeGenerationSettings.Builder bgs = new BiomeGenerationSettings.Builder().surfaceBuilder(csb);
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
