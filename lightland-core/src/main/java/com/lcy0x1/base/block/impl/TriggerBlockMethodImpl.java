package com.lcy0x1.base.block.impl;

import com.lcy0x1.base.block.mult.CreateBlockStateBlockMethod;
import com.lcy0x1.base.block.mult.DefaultStateBlockMethod;
import com.lcy0x1.base.block.mult.NeighborUpdateBlockMethod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TriggerBlockMethodImpl implements NeighborUpdateBlockMethod, CreateBlockStateBlockMethod, DefaultStateBlockMethod {

    private final int delay;

    public TriggerBlockMethodImpl(int delay) {
        this.delay = delay;
    }

    @Override
    public void neighborChanged(Block self, BlockState state, World world, BlockPos pos, Block nei_block, BlockPos nei_pos, boolean moving) {
        boolean flag = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
        boolean flag1 = state.getValue(BlockStateProperties.TRIGGERED);
        if (flag && !flag1) {
            world.getBlockTicks().scheduleTick(pos, self, delay);
            world.setBlock(pos, state.setValue(BlockStateProperties.TRIGGERED, Boolean.TRUE), delay);
        } else if (!flag && flag1) {
            world.setBlock(pos, state.setValue(BlockStateProperties.TRIGGERED, Boolean.FALSE), delay);
        }
    }

    @Override
    public void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.TRIGGERED);
    }

    @Override
    public BlockState getDefaultState(BlockState state) {
        return state.setValue(BlockStateProperties.TRIGGERED, false);
    }
}
