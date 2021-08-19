package com.hikarishima.lightland.magic.registry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class TempBlock extends Block {

    public TempBlock(Properties props) {
        super(props);
    }

    public static void putBlock(ServerWorld world, BlockPos pos, BlockState state, int tick) {
        BlockState bs = world.getBlockState(pos);
        if (bs.isAir()) {
            world.setBlock(pos, state, 3);
            world.getBlockTicks().scheduleTick(pos, state.getBlock(), tick);
        }
    }

    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

}
