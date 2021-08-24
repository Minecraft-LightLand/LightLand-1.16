package com.lcy0x1.base.proxy.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IPower extends IImpl {
    int getSignal(BlockState bs, IBlockReader r, BlockPos pos, Direction d);
}