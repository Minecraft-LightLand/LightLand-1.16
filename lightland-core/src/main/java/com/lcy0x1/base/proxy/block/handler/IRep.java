package com.lcy0x1.base.proxy.block.handler;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRep extends IImpl {
    void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving);
}