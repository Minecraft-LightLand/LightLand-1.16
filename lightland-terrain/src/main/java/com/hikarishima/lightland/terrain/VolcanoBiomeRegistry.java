package com.hikarishima.lightland.terrain;

import com.hikarishima.lightland.terrain.config.VolcanoBiomeReader;
import com.hikarishima.lightland.terrain.feature.*;
import com.hikarishima.lightland.terrain.surfacebuilder.LavaBeachSurfaceBuilder;
import com.hikarishima.lightland.terrain.surfacebuilder.LavaLakeSurfaceBuilder;
import com.hikarishima.lightland.terrain.surfacebuilder.VolcanoSideSurfaceBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;

public class VolcanoBiomeRegistry {

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

    public static final LavaSmokePlacement P_SMOKE = reg("smoke", new LavaSmokePlacement(LavaSmokePlacementConfig.CODEC));
    public static final LavaLakeSmokePlacement P_LAKE = reg("lava_lake_smoke", new LavaLakeSmokePlacement(LavaLakeSmokePlacementConfig.CODEC));
    public static final LavaSmokeFeature F_SMOKE = reg("smoke", new LavaSmokeFeature(LavaSmokeFeatureConfig.CODEC));
    public static final LavaLakeSmokeFeature F_LAKE = reg("lava_lake_smoke", new LavaLakeSmokeFeature(NoFeatureConfig.CODEC));
    public static final ConfiguredPlacement<?> CP_SMOKE, CP_LAKE;
    public static final ConfiguredFeature<?, ?> CF_SMOKE, CF_LAKE;

    public static final Biome VOLCANO_LAVA, VOLCANO_BEACH, VOLCANO_TOP;
    public static final Biome[] VOLCANO_SIDE;

    static {
        VolcanoBiomeReader.init();
        VolcanoBiomeReader.VolcanoConfig c = VolcanoBiomeReader.CONFIG;
        if (c == null || c.lava_well == null) {
            LogManager.getLogger().fatal("volcano biome config not loaded");
        }
        CP_SMOKE = P_SMOKE.configured(new LavaSmokePlacementConfig(c.lava_well.chance));
        CP_LAKE = P_LAKE.configured(new LavaLakeSmokePlacementConfig(c.lava_well.lava_lake_chance));
        LavaSmokeFeatureConfig fc = new LavaSmokeFeatureConfig(c.lava_well);
        CF_SMOKE = reg("smoke", F_SMOKE.configured(fc).decorated(CP_SMOKE));
        CF_LAKE = reg("lava_lake_smoke", F_LAKE.configured(NoFeatureConfig.INSTANCE).decorated(CP_LAKE));

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
                        b.getRegistryName().equals(VOLCANO_BEACH.getRegistryName()));
    }

    private static <V extends T, T extends ForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(LightLandTerrain.MODID, name);
        return v;
    }

    private static <SC extends ISurfaceBuilderConfig> ConfiguredSurfaceBuilder<SC> reg(String name, ConfiguredSurfaceBuilder<SC> csb) {
        return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_SURFACE_BUILDER, LightLandTerrain.MODID + ":" + name, csb);
    }

    private static <FC extends IFeatureConfig, F extends Feature<FC>> ConfiguredFeature<FC, F> reg(String name, ConfiguredFeature<FC, F> cf) {
        return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_FEATURE, LightLandTerrain.MODID + ":" + name, cf);
    }

    private static Biome genVolcanoLavaBiome(float depth, float scale, ConfiguredSurfaceBuilder<SurfaceBuilderConfig> csb) {
        BiomeGenerationSettings.Builder bgs = new BiomeGenerationSettings.Builder().surfaceBuilder(csb);
        DefaultBiomeFeatures.addBadlandGrass(bgs);
        DefaultBiomeFeatures.addBadlandExtraVegetation(bgs);
        DefaultBiomeFeatures.addDefaultSprings(bgs);
        if (csb == CSB_VOL_BASE)
            bgs.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, CF_SMOKE);
        if (csb == CSB_LAVA_LAKE)
            bgs.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, CF_LAKE);

        return (new Biome.Builder())
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.MESA)
                .depth(depth).scale(scale).temperature(2.0F).downfall(0.0F)
                .specialEffects(new BiomeAmbience.Builder()
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.WHITE_ASH, 0.118093334F))
                        .waterColor(4159204).waterFogColor(329011)
                        .fogColor(6840176).skyColor(calculateSkyColor(2.0F))
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
