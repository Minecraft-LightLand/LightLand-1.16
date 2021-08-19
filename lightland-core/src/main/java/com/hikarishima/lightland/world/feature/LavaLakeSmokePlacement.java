package com.hikarishima.lightland.world.feature;

import com.hikarishima.lightland.config.worldgen.VolcanoBiomeReader;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.stream.Stream;

public class LavaLakeSmokePlacement extends Placement<LavaLakeSmokePlacementConfig> {

    public static final BlockState DEF = Blocks.LAVA.defaultBlockState();

    public LavaLakeSmokePlacement(Codec<LavaLakeSmokePlacementConfig> p_i232086_1_) {
        super(p_i232086_1_);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random random, LavaLakeSmokePlacementConfig config, BlockPos pos) {
        Stream.Builder<BlockPos> b = Stream.builder();
        for (int x = 0; x < 16; x++)
            for (int z = 0; z < 16; z++) {
                if (random.nextFloat() < config.chance) {
                    BlockPos p = new BlockPos(pos.getX() + x, VolcanoBiomeReader.CONFIG.lava_level - 1, pos.getZ() + z);
                    if (helper.getBlockState(p) == DEF)
                        b.accept(p);
                }
            }
        return b.build();
    }
}
