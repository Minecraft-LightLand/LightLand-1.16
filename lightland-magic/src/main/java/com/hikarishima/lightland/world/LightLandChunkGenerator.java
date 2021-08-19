package com.hikarishima.lightland.world;

import com.hikarishima.lightland.config.road.ImageRoadReader;
import com.hikarishima.lightland.config.worldgen.ImageBiomeReader;
import com.hikarishima.lightland.config.worldgen.VolcanoBiomeReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.world.StructureSpawnManager;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class LightLandChunkGenerator extends ChunkGenerator {

    public static final Codec<LightLandChunkGenerator> CODEC = RecordCodecBuilder.create((e0) -> e0.group(
            BiomeProvider.CODEC.fieldOf("biome_source").forGetter((e1) -> e1.biomeSource),
            Codec.LONG.fieldOf("seed").stable().forGetter((e1) -> e1.seed),
            DimensionSettings.CODEC.fieldOf("settings").forGetter((e1) -> e1.settings)
    ).apply(e0, e0.stable(LightLandChunkGenerator::new)));
    public static final float[] BIOME_WEIGHTS = Util.make(new float[25], (ans) -> {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f = 10.0F / MathHelper.sqrt(i * i + j * j + 0.2F);
                ans[i + 2 + (j + 2) * 5] = f;
            }
        }

    });
    private static final float[] BEARD_KERNEL = Util.make(new float[13824], (p_236094_0_) -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    p_236094_0_[i * 24 * 24 + j * 24 + k] = (float) computeContribution(j - 12, k - 12, i - 12);
                }
            }
        }

    });
    private static final BlockState AIR;

    static {
        AIR = Blocks.AIR.defaultBlockState();
    }

    protected final SharedSeedRandom random;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    protected final Supplier<DimensionSettings> settings;
    private final int chunkHeight;
    private final int chunkWidth;
    private final int chunkCountX;
    private final int chunkCountY;
    private final int chunkCountZ;
    private final OctavesNoiseGenerator minLimitPerlinNoise;
    private final OctavesNoiseGenerator maxLimitPerlinNoise;
    private final OctavesNoiseGenerator mainPerlinNoise;
    private final INoiseGenerator surfaceNoise;
    private final OctavesNoiseGenerator depthNoise;
    @Nullable
    private final SimplexNoiseGenerator islandNoise;
    private final long seed;
    private final int height;

    public LightLandChunkGenerator(BiomeProvider p_i241975_1_, long p_i241975_2_, Supplier<DimensionSettings> p_i241975_4_) {
        this(p_i241975_1_, p_i241975_1_, p_i241975_2_, p_i241975_4_);
    }

    private LightLandChunkGenerator(BiomeProvider p_i241976_1_, BiomeProvider p_i241976_2_, long p_i241976_3_, Supplier<DimensionSettings> p_i241976_5_) {
        super(p_i241976_1_, p_i241976_2_, p_i241976_5_.get().structureSettings(), p_i241976_3_);
        this.seed = p_i241976_3_;
        DimensionSettings dimensionsettings = p_i241976_5_.get();
        this.settings = p_i241976_5_;
        NoiseSettings noisesettings = dimensionsettings.noiseSettings();
        this.height = noisesettings.height();
        this.chunkHeight = noisesettings.noiseSizeVertical() * 4;
        this.chunkWidth = noisesettings.noiseSizeHorizontal() * 4;
        this.defaultBlock = dimensionsettings.getDefaultBlock();
        this.defaultFluid = dimensionsettings.getDefaultFluid();
        this.chunkCountX = 16 / this.chunkWidth;
        this.chunkCountY = noisesettings.height() / this.chunkHeight;
        this.chunkCountZ = 16 / this.chunkWidth;
        this.random = new SharedSeedRandom(p_i241976_3_);
        this.minLimitPerlinNoise = new OctavesNoiseGenerator(this.random, IntStream.rangeClosed(-15, 0));
        this.maxLimitPerlinNoise = new OctavesNoiseGenerator(this.random, IntStream.rangeClosed(-15, 0));
        this.mainPerlinNoise = new OctavesNoiseGenerator(this.random, IntStream.rangeClosed(-7, 0));
        this.surfaceNoise = noisesettings.useSimplexSurfaceNoise() ? new PerlinNoiseGenerator(this.random, IntStream.rangeClosed(-3, 0)) : new OctavesNoiseGenerator(this.random, IntStream.rangeClosed(-3, 0));
        this.random.consumeCount(2620);
        this.depthNoise = new OctavesNoiseGenerator(this.random, IntStream.rangeClosed(-15, 0));
        if (noisesettings.islandNoiseOverride()) {
            SharedSeedRandom sharedseedrandom = new SharedSeedRandom(p_i241976_3_);
            sharedseedrandom.consumeCount(17292);
            this.islandNoise = new SimplexNoiseGenerator(sharedseedrandom);
        } else {
            this.islandNoise = null;
        }

    }

    private static double getContribution(int p_222556_0_, int p_222556_1_, int p_222556_2_) {
        int i = p_222556_0_ + 12;
        int j = p_222556_1_ + 12;
        int k = p_222556_2_ + 12;
        if (i >= 0 && i < 24) {
            if (j >= 0 && j < 24) {
                return k >= 0 && k < 24 ? (double) BEARD_KERNEL[k * 24 * 24 + i * 24 + j] : 0.0D;
            } else {
                return 0.0D;
            }
        } else {
            return 0.0D;
        }
    }

    private static double computeContribution(int p_222554_0_, int p_222554_1_, int p_222554_2_) {
        double d0 = p_222554_0_ * p_222554_0_ + p_222554_2_ * p_222554_2_;
        double d1 = (double) p_222554_1_ + 0.5D;
        double d2 = d1 * d1;
        double d3 = Math.pow(2.718281828459045D, -(d2 / 16.0D + d0 / 16.0D));
        double d4 = -d1 * MathHelper.fastInvSqrt(d2 / 2.0D + d0 / 2.0D) / 2.0D;
        return d4 * d3;
    }

    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @OnlyIn(Dist.CLIENT)
    public ChunkGenerator withSeed(long p_230349_1_) {
        return new NoiseChunkGenerator(this.biomeSource.withSeed(p_230349_1_), p_230349_1_, this.settings);
    }

    private double sampleAndClampNoise(int p_222552_1_, int p_222552_2_, int p_222552_3_, double p_222552_4_, double p_222552_6_, double p_222552_8_, double p_222552_10_) {
        double d0 = 0.0D;
        double d1 = 0.0D;
        double d2 = 0.0D;
        boolean flag = true;
        double d3 = 1.0D;

        for (int i = 0; i < 16; ++i) {
            double d4 = OctavesNoiseGenerator.wrap((double) p_222552_1_ * p_222552_4_ * d3);
            double d5 = OctavesNoiseGenerator.wrap((double) p_222552_2_ * p_222552_6_ * d3);
            double d6 = OctavesNoiseGenerator.wrap((double) p_222552_3_ * p_222552_4_ * d3);
            double d7 = p_222552_6_ * d3;
            ImprovedNoiseGenerator improvednoisegenerator = this.minLimitPerlinNoise.getOctaveNoise(i);
            if (improvednoisegenerator != null) {
                d0 += improvednoisegenerator.noise(d4, d5, d6, d7, (double) p_222552_2_ * d7) / d3;
            }

            ImprovedNoiseGenerator improvednoisegenerator1 = this.maxLimitPerlinNoise.getOctaveNoise(i);
            if (improvednoisegenerator1 != null) {
                d1 += improvednoisegenerator1.noise(d4, d5, d6, d7, (double) p_222552_2_ * d7) / d3;
            }

            if (i < 8) {
                ImprovedNoiseGenerator improvednoisegenerator2 = this.mainPerlinNoise.getOctaveNoise(i);
                if (improvednoisegenerator2 != null) {
                    d2 += improvednoisegenerator2.noise(OctavesNoiseGenerator.wrap((double) p_222552_1_ * p_222552_8_ * d3), OctavesNoiseGenerator.wrap((double) p_222552_2_ * p_222552_10_ * d3), OctavesNoiseGenerator.wrap((double) p_222552_3_ * p_222552_8_ * d3), p_222552_10_ * d3, (double) p_222552_2_ * p_222552_10_ * d3) / d3;
                }
            }

            d3 /= 2.0D;
        }

        return MathHelper.clampedLerp(d0 / 512.0D, d1 / 512.0D, (d2 / 10.0D + 1.0D) / 2.0D);
    }

    private double[] makeAndFillNoiseColumn(int p_222547_1_, int p_222547_2_) {
        double[] adouble = new double[this.chunkCountY + 1];
        this.fillNoiseColumn(adouble, p_222547_1_, p_222547_2_);
        return adouble;
    }

    private void fillNoiseColumn(double[] p_222548_1_, int x, int z) {
        NoiseSettings noisesettings = this.settings.get().noiseSettings();
        double d0;
        double d1;
        double d17;
        double d19;
        if (this.islandNoise != null) {
            d0 = EndBiomeProvider.getHeightValue(this.islandNoise, x, z) - 8.0F;
            if (d0 > 0.0D) {
                d1 = 0.25D;
            } else {
                d1 = 1.0D;
            }
        } else {
            float f = 0.0F;
            float f1 = 0.0F;
            float f2 = 0.0F;
            float self_depth = ImageBiomeReader.getDepth(biomeSource, x, z);

            for (int k = -2; k <= 2; ++k) {
                for (int l = -2; l <= 2; ++l) {
                    float depth = ImageBiomeReader.getDepth(biomeSource, x + k, z + l);
                    float scale = ImageBiomeReader.getScale(biomeSource, x + k, z + l);

                    float v = depth > self_depth ? 0.5F : 1.0F;
                    float weight = v * BIOME_WEIGHTS[k + 2 + (l + 2) * 5] / (depth + 2.0F);
                    f += scale * weight;
                    f1 += depth * weight;
                    f2 += weight;
                }
            }

            float f10 = f1 / f2;
            float f11 = f / f2;
            d17 = f10 * 0.5F - 0.125F;
            d19 = f11 * 0.9F + 0.1F;
            d0 = d17 * 0.265625D;
            d1 = 96.0D / d19;
        }

        double d12 = 684.412D * noisesettings.noiseSamplingSettings().xzScale();
        double d13 = 684.412D * noisesettings.noiseSamplingSettings().yScale();
        double d14 = d12 / noisesettings.noiseSamplingSettings().xzFactor();
        double d15 = d13 / noisesettings.noiseSamplingSettings().yFactor();
        d17 = noisesettings.topSlideSettings().target();
        d19 = noisesettings.topSlideSettings().size();
        double d20 = noisesettings.topSlideSettings().offset();
        double d21 = noisesettings.bottomSlideSettings().target();
        double d2 = noisesettings.bottomSlideSettings().size();
        double d3 = noisesettings.bottomSlideSettings().offset();
        double d4 = noisesettings.randomDensityOffset() ? this.getRandomDensity(x, z) : 0.0D;
        double d5 = noisesettings.densityFactor();
        double d6 = noisesettings.densityOffset();

        for (int i1 = 0; i1 <= this.chunkCountY; ++i1) {
            double d7 = this.sampleAndClampNoise(x, i1, z, d12, d13, d14, d15);
            double d8 = 1.0D - (double) i1 * 2.0D / (double) this.chunkCountY + d4;
            double d9 = d8 * d5 + d6;
            double d10 = (d9 + d0) * d1;
            if (d10 > 0.0D) {
                d7 += d10 * 4.0D;
            } else {
                d7 += d10;
            }

            double d22;
            if (d19 > 0.0D) {
                d22 = ((double) (this.chunkCountY - i1) - d20) / d19;
                d7 = MathHelper.clampedLerp(d17, d7, d22);
            }

            if (d2 > 0.0D) {
                d22 = ((double) i1 - d3) / d2;
                d7 = MathHelper.clampedLerp(d21, d7, d22);
            }

            p_222548_1_[i1] = d7;
        }

    }

    private double getRandomDensity(int p_236095_1_, int p_236095_2_) {
        double d0 = this.depthNoise.getValue(p_236095_1_ * 200, 10.0D, p_236095_2_ * 200, 1.0D, 0.0D, true);
        double d1;
        if (d0 < 0.0D) {
            d1 = -d0 * 0.3D;
        } else {
            d1 = d0;
        }

        double d2 = d1 * 24.575625D - 2.0D;
        return d2 < 0.0D ? d2 * 0.009486607142857142D : Math.min(d2, 1.0D) * 0.006640625D;
    }

    public int getBaseHeight(int p_222529_1_, int p_222529_2_, Type p_222529_3_) {
        return this.iterateNoiseColumn(p_222529_1_, p_222529_2_, null, p_222529_3_.isOpaque());
    }

    public IBlockReader getBaseColumn(int p_230348_1_, int p_230348_2_) {
        BlockState[] ablockstate = new BlockState[this.chunkCountY * this.chunkHeight];
        this.iterateNoiseColumn(p_230348_1_, p_230348_2_, ablockstate, null);
        return new Blockreader(ablockstate);
    }

    private int iterateNoiseColumn(int p_236087_1_, int p_236087_2_, @Nullable BlockState[] p_236087_3_, @Nullable Predicate<BlockState> p_236087_4_) {
        int i = Math.floorDiv(p_236087_1_, this.chunkWidth);
        int j = Math.floorDiv(p_236087_2_, this.chunkWidth);
        int k = Math.floorMod(p_236087_1_, this.chunkWidth);
        int l = Math.floorMod(p_236087_2_, this.chunkWidth);
        double d0 = (double) k / (double) this.chunkWidth;
        double d1 = (double) l / (double) this.chunkWidth;
        double[][] adouble = new double[][]{this.makeAndFillNoiseColumn(i, j), this.makeAndFillNoiseColumn(i, j + 1), this.makeAndFillNoiseColumn(i + 1, j), this.makeAndFillNoiseColumn(i + 1, j + 1)};

        for (int i1 = this.chunkCountY - 1; i1 >= 0; --i1) {
            double d2 = adouble[0][i1];
            double d3 = adouble[1][i1];
            double d4 = adouble[2][i1];
            double d5 = adouble[3][i1];
            double d6 = adouble[0][i1 + 1];
            double d7 = adouble[1][i1 + 1];
            double d8 = adouble[2][i1 + 1];
            double d9 = adouble[3][i1 + 1];

            for (int j1 = this.chunkHeight - 1; j1 >= 0; --j1) {
                double d10 = (double) j1 / (double) this.chunkHeight;
                double d11 = MathHelper.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
                int k1 = i1 * this.chunkHeight + j1;
                BlockState blockstate = this.generateBaseState(d11, k1);
                if (p_236087_3_ != null) {
                    p_236087_3_[k1] = blockstate;
                }

                if (p_236087_4_ != null && p_236087_4_.test(blockstate)) {
                    return k1 + 1;
                }
            }
        }

        return 0;
    }

    protected BlockState generateBaseState(double p_236086_1_, int p_236086_3_) {
        BlockState blockstate;
        if (p_236086_1_ > 0.0D) {
            blockstate = this.defaultBlock;
        } else if (p_236086_3_ < this.getSeaLevel()) {
            blockstate = this.defaultFluid;
        } else {
            blockstate = AIR;
        }

        return blockstate;
    }

    public void buildSurfaceAndBedrock(WorldGenRegion region, IChunk c) {
        ChunkPos chunkpos = c.getPos();
        int i = chunkpos.x;
        int j = chunkpos.z;
        SharedSeedRandom r = new SharedSeedRandom();
        r.setBaseChunkSeed(i, j);
        ChunkPos cPos = c.getPos();
        int pcx = cPos.getMinBlockX();
        int pcz = cPos.getMinBlockZ();
        double d0 = 0.0625D;
        Mutable pos = new Mutable();

        for (int ix = 0; ix < 16; ++ix) {
            for (int iz = 0; iz < 16; ++iz) {
                int x = pcx + ix;
                int z = pcz + iz;
                int y = c.getHeight(Type.WORLD_SURFACE_WG, ix, iz) + 1;
                double d1 = this.surfaceNoise.getSurfaceNoiseValue(x * d0, z * d0, d0, ix * d0) * 15.0D;
                region.getBiome(pos.set(pcx + ix, y, pcz + iz)).buildSurfaceAt(r, c, x, z, y, d1, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), region.getSeed());
                ImageRoadReader.buildRoadSurface(x, z, r, c, pos.below());
            }
        }
    }

    public void fillFromNoise(IWorld world, StructureManager manager, IChunk chunk) {
        VolcanoBiomeReader.init();

        ObjectList<StructurePiece> pieceList = new ObjectArrayList<>(10);
        ObjectList<JigsawJunction> jigsawList = new ObjectArrayList<>(32);
        ChunkPos chunkpos = chunk.getPos();
        int cx = chunkpos.x;
        int cz = chunkpos.z;
        int cpx = cx << 4;
        int cpz = cz << 4;

        for (Structure<?> value : Structure.NOISE_AFFECTING_FEATURES) {
            manager.startsForFeature(SectionPos.of(chunkpos, 0), value).forEach((p_236089_5_) -> {
                Iterator<StructurePiece> var6 = p_236089_5_.getPieces().iterator();

                while (true) {
                    StructurePiece structurepiece1;
                    do {
                        if (!var6.hasNext()) {
                            return;
                        }

                        structurepiece1 = var6.next();
                    } while (!structurepiece1.isCloseToChunk(chunkpos, 12));

                    if (structurepiece1 instanceof AbstractVillagePiece) {
                        AbstractVillagePiece abstractvillagepiece = (AbstractVillagePiece) structurepiece1;
                        JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour = abstractvillagepiece.getElement().getProjection();
                        if (jigsawpattern$placementbehaviour == JigsawPattern.PlacementBehaviour.RIGID) {
                            pieceList.add(abstractvillagepiece);
                        }

                        for (JigsawJunction jigsawjunction1 : abstractvillagepiece.getJunctions()) {
                            int l5 = jigsawjunction1.getSourceX();
                            int i6 = jigsawjunction1.getSourceZ();
                            if (l5 > cpx - 12 && i6 > cpz - 12 && l5 < cpx + 15 + 12 && i6 < cpz + 15 + 12) {
                                jigsawList.add(jigsawjunction1);
                            }
                        }
                    } else {
                        pieceList.add(structurepiece1);
                    }

                }
            });
        }

        double[][][] adouble = new double[2][this.chunkCountZ + 1][this.chunkCountY + 1];

        for (int i5 = 0; i5 < this.chunkCountZ + 1; ++i5) {
            adouble[0][i5] = new double[this.chunkCountY + 1];
            this.fillNoiseColumn(adouble[0][i5], cx * this.chunkCountX, cz * this.chunkCountZ + i5);
            adouble[1][i5] = new double[this.chunkCountY + 1];
        }

        ChunkPrimer chunkprimer = (ChunkPrimer) chunk;
        Heightmap ocean_floor = chunkprimer.getOrCreateHeightmapUnprimed(Type.OCEAN_FLOOR_WG);
        Heightmap surface = chunkprimer.getOrCreateHeightmapUnprimed(Type.WORLD_SURFACE_WG);
        Mutable pos = new Mutable();
        ObjectListIterator<StructurePiece> pieceItr = pieceList.iterator();
        ObjectListIterator<JigsawJunction> jigsawItr = jigsawList.iterator();

        for (int icx = 0; icx < this.chunkCountX; ++icx) {
            int icz;
            for (icz = 0; icz < this.chunkCountZ + 1; ++icz) {
                this.fillNoiseColumn(adouble[1][icz], cx * this.chunkCountX + icx + 1, cz * this.chunkCountZ + icz);
            }

            for (icz = 0; icz < this.chunkCountZ; ++icz) {
                ChunkSection chunksection = chunkprimer.getOrCreateSection(15);
                chunksection.acquire();

                Biome[][] biomes = new Biome[4][4];
                boolean[][] isLava = new boolean[4][4];
                for (int bx = 0; bx < 4; bx++)
                    for (int bz = 0; bz < 4; bz++) {
                        int ibx = (cpx + icx * this.chunkWidth) / 4 + bx;
                        int ibz = (cpz + icz * this.chunkWidth) / 4 + bz;
                        biomes[bx][bz] = this.biomeSource.getNoiseBiome(ibx, 0, ibz);
                        isLava[bx][bz] = VolcanoBiomeRegistry.isLavaLakeBiome(biomes[bx][bz]);
                    }

                for (int icy = this.chunkCountY - 1; icy >= 0; --icy) {
                    double d0 = adouble[0][icz][icy];
                    double d1 = adouble[0][icz + 1][icy];
                    double d2 = adouble[1][icz][icy];
                    double d3 = adouble[1][icz + 1][icy];
                    double d4 = adouble[0][icz][icy + 1];
                    double d5 = adouble[0][icz + 1][icy + 1];
                    double d6 = adouble[1][icz][icy + 1];
                    double d7 = adouble[1][icz + 1][icy + 1];

                    for (int ipy = this.chunkHeight - 1; ipy >= 0; --ipy) {
                        int py = icy * this.chunkHeight + ipy;
                        int pcy = py & 15;
                        int cpy = py >> 4;
                        if (chunksection.bottomBlockY() >> 4 != cpy) {
                            chunksection.release();
                            chunksection = chunkprimer.getOrCreateSection(cpy);
                            chunksection.acquire();
                        }

                        double d8 = (double) ipy / (double) this.chunkHeight;
                        double d9 = MathHelper.lerp(d8, d0, d4);
                        double d10 = MathHelper.lerp(d8, d2, d6);
                        double d11 = MathHelper.lerp(d8, d1, d5);
                        double d12 = MathHelper.lerp(d8, d3, d7);

                        for (int ipx = 0; ipx < this.chunkWidth; ++ipx) {
                            int px = cpx + icx * this.chunkWidth + ipx;
                            int pcx = px & 15;
                            double d13 = (double) ipx / (double) this.chunkWidth;
                            double d14 = MathHelper.lerp(d13, d9, d10);
                            double d15 = MathHelper.lerp(d13, d11, d12);

                            for (int ipz = 0; ipz < this.chunkWidth; ++ipz) {
                                int pz = cpz + icz * this.chunkWidth + ipz;
                                int pcz = pz & 15;
                                double d16 = (double) ipz / (double) this.chunkWidth;
                                double d17 = MathHelper.lerp(d16, d14, d15);
                                double d18 = MathHelper.clamp(d17 / 200.0D, -1.0D, 1.0D);

                                int j4;
                                int k4;
                                int l4;
                                for (d18 = d18 / 2.0D - d18 * d18 * d18 / 24.0D; pieceItr.hasNext(); d18 += getContribution(j4, k4, l4) * 0.8D) {
                                    StructurePiece structurepiece = pieceItr.next();
                                    MutableBoundingBox mutableboundingbox = structurepiece.getBoundingBox();
                                    j4 = Math.max(0, Math.max(mutableboundingbox.x0 - px, px - mutableboundingbox.x1));
                                    k4 = py - (mutableboundingbox.y0 + (structurepiece instanceof AbstractVillagePiece ? ((AbstractVillagePiece) structurepiece).getGroundLevelDelta() : 0));
                                    l4 = Math.max(0, Math.max(mutableboundingbox.z0 - pz, pz - mutableboundingbox.z1));
                                }

                                pieceItr.back(pieceList.size());

                                while (jigsawItr.hasNext()) {
                                    JigsawJunction jigsawjunction = jigsawItr.next();
                                    int k5 = px - jigsawjunction.getSourceX();
                                    j4 = py - jigsawjunction.getSourceGroundY();
                                    k4 = pz - jigsawjunction.getSourceZ();
                                    d18 += getContribution(k5, j4, k4) * 0.4D;
                                }

                                jigsawItr.back(jigsawList.size());
                                BlockState blockstate = this.generateBaseState(d18, py);

                                pos.set(px, py, pz);

                                if (blockstate != this.defaultBlock && py <= VolcanoBiomeReader.CONFIG.lava_level) {
                                    if (isLava[ipx / 4][ipz / 4])
                                        blockstate = Blocks.LAVA.defaultBlockState();
                                }
                                if (blockstate != AIR) {
                                    if (blockstate.getLightValue(chunkprimer, pos) != 0) {
                                        chunkprimer.addLight(pos);
                                    }
                                    chunksection.setBlockState(pcx, pcy, pcz, blockstate, false);
                                    ocean_floor.update(pcx, py, pcz, blockstate);
                                    surface.update(pcx, py, pcz, blockstate);
                                }
                            }
                        }
                    }
                }

                chunksection.release();
            }

            double[][] adouble1 = adouble[0];
            adouble[0] = adouble[1];
            adouble[1] = adouble1;
        }

    }

    public int getGenDepth() {
        return this.height;
    }

    public int getSeaLevel() {
        return this.settings.get().seaLevel();
    }

    public List<Spawners> getMobsAt(Biome b, StructureManager sm, EntityClassification ec, BlockPos pos) {
        List<Spawners> spawns = StructureSpawnManager.getStructureSpawns(sm, ec, pos);
        return spawns != null ? spawns : super.getMobsAt(b, sm, ec, pos);
    }

    public void spawnOriginalMobs(WorldGenRegion r) {
        int i = r.getCenterX();
        int j = r.getCenterZ();
        Biome biome = r.getBiome((new ChunkPos(i, j)).getWorldPosition());
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
        sharedseedrandom.setDecorationSeed(r.getSeed(), i << 4, j << 4);
        WorldEntitySpawner.spawnMobsForChunkGeneration(r, biome, i, j, sharedseedrandom);
    }
}
