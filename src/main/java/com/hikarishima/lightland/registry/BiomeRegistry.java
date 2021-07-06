package com.hikarishima.lightland.registry;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.config.VolcanoBiomeReader;
import com.hikarishima.lightland.world.LavaBeachSurfaceBuilder;
import com.hikarishima.lightland.world.LavaLakeSurfaceBuilder;
import com.hikarishima.lightland.world.VolcanoSideSurfaceBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.surfacebuilders.*;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeRegistry {

    public static final BlockState BS_DEF = Blocks.BASALT.defaultBlockState();
    public static final BlockState BS_BASE = Blocks.BASALT.defaultBlockState();
    public static final BlockState BS_MAGMA = Blocks.MAGMA_BLOCK.defaultBlockState();
    public static final SurfaceBuilderConfig SBC_VOLCANO = new SurfaceBuilderConfig(BS_DEF, BS_BASE, BS_MAGMA);

    public static final SurfaceBuilder<SurfaceBuilderConfig> SB_LAVA_LAKE = reg("lava_lake", new LavaLakeSurfaceBuilder(SurfaceBuilderConfig.CODEC));
    public static final SurfaceBuilder<SurfaceBuilderConfig> SB_LAVA_BEACH = reg("lava_beach", new LavaBeachSurfaceBuilder(SurfaceBuilderConfig.CODEC));
    public static final SurfaceBuilder<SurfaceBuilderConfig> SB_VOL_BASE = reg("volcano_top", new VolcanoSideSurfaceBuilder(SurfaceBuilderConfig.CODEC));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> CSB_LAVA_LAKE = reg("lava_lake", SB_LAVA_LAKE.configured(SBC_VOLCANO));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> CSB_LAVA_BEACH = reg("lava_beach", SB_LAVA_BEACH.configured(SBC_VOLCANO));
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> CSB_VOL_BASE = reg("volcano_top", SB_VOL_BASE.configured(SBC_VOLCANO));

    public static final Biome VOLCANO_LAVA, VOLCANO_BEACH, VOLCANO_TOP;
    public static final Biome[] VOLCANO_SIDE;

    static {
        VolcanoBiomeReader.init();
        VolcanoBiomeReader.VolcanoConfig c = VolcanoBiomeReader.CONFIG;
        VOLCANO_LAVA = reg("lava_lake", genVolcanoLavaBiome(c.max - 1, 0f, CSB_LAVA_LAKE));
        VOLCANO_BEACH = reg("lava_beach", genVolcanoLavaBiome(c.max, 0f, CSB_LAVA_BEACH));
        VOLCANO_TOP = reg("volcano_top", genVolcanoLavaBiome(c.max, 0f, CSB_VOL_BASE));
        VOLCANO_SIDE = new Biome[c.side_count];
        for (int i = 0; i < c.side_count; i++) {
            VOLCANO_SIDE[i] = reg("volcano_side_" + i, genVolcanoLavaBiome(c.max - c.step * (i + 1), c.scale, CSB_VOL_BASE));
        }
    }

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

    private static Biome genVolcanoLavaBiome(float depth, float scale, ConfiguredSurfaceBuilder<SurfaceBuilderConfig> csb) {
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
