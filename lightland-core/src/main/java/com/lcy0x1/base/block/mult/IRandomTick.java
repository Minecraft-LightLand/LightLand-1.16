package com.lcy0x1.base.block.mult;

import com.lcy0x1.base.block.type.IMultImpl;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public interface IRandomTick extends IMultImpl {

    void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random);

}
