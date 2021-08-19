package com.hikarishima.lightland.terrain.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class LavaLakeSmokeFeature extends Feature<NoFeatureConfig> {

    public static final BlockState CAMPFIRE = Blocks.CAMPFIRE.defaultBlockState();
    public static final BlockState HAYBALE = Blocks.HAY_BLOCK.defaultBlockState();

    public LavaLakeSmokeFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(
            ISeedReader w, ChunkGenerator gen, Random random,
            BlockPos pos, NoFeatureConfig config) {
        w.setBlock(pos, CAMPFIRE, 3);
        w.setBlock(pos.below(), HAYBALE, 3);
        return true;
    }
}
