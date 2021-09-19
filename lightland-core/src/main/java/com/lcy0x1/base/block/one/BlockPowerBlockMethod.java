package com.lcy0x1.base.block.one;

import com.lcy0x1.base.block.type.SingletonBlockMethod;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface BlockPowerBlockMethod extends SingletonBlockMethod {

    int getSignal(BlockState bs, IBlockReader r, BlockPos pos, Direction d);
}