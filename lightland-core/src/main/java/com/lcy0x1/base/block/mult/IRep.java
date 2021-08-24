package com.lcy0x1.base.block.mult;

import com.lcy0x1.base.block.type.IMultImpl;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRep extends IMultImpl {
    void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving);
}