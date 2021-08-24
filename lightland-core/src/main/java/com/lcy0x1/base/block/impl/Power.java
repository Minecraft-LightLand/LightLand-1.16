package com.lcy0x1.base.block.impl;

import com.lcy0x1.base.block.one.IPower;
import com.lcy0x1.base.block.mult.IState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class Power implements IState, IPower {

    public Power() {
    }

    @Override
    public void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.POWER);
    }

    @Override
    public int getSignal(BlockState bs, IBlockReader r, BlockPos pos, Direction d) {
        return bs.getValue(BlockStateProperties.POWER);
    }

}
