package com.lcy0x1.base.block.one;

import com.lcy0x1.base.block.type.IOneImpl;
import com.lcy0x1.base.proxy.annotation.Singleton;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

@Singleton
public interface IPower extends IOneImpl {
    default boolean isSignalSource(BlockState bs) {
        return true;
    }

    int getSignal(BlockState bs, IBlockReader r, BlockPos pos, Direction d);
}