package com.hikarishima.lightland.terrain.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.stream.Stream;

public class LavaSmokePlacement extends Placement<LavaSmokePlacementConfig> {

    public static final BlockState DEF = Blocks.BASALT.defaultBlockState();

    public LavaSmokePlacement(Codec<LavaSmokePlacementConfig> p_i232086_1_) {
        super(p_i232086_1_);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random random, LavaSmokePlacementConfig config, BlockPos pos) {
        if (random.nextFloat() < config.chance) {
            int x = pos.getX() + random.nextInt(16);
            int z = pos.getZ() + random.nextInt(16);
            int y = helper.getHeight(Heightmap.Type.MOTION_BLOCKING, x, z);
            BlockPos p = new BlockPos(x, y, z);
            if (helper.getBlockState(p.below()) == DEF)
                return Stream.of(p);
        }
        return Stream.empty();
    }
}
