package com.lcy0x1.base.block.impl;

import com.lcy0x1.base.block.BaseBlock;
import com.lcy0x1.base.block.BlockProxy;
import com.lcy0x1.base.block.one.IFace;
import com.lcy0x1.base.block.one.IRotMir;
import com.lcy0x1.base.block.mult.IState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public class HorizontalBlock implements IRotMir, IState, IFace {

    public HorizontalBlock() {
    }

    @Override
    public void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockProxy.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockState def, BlockItemUseContext context) {
        return def.setValue(BlockProxy.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(BlockProxy.HORIZONTAL_FACING)));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(BlockProxy.HORIZONTAL_FACING, rot.rotate(state.getValue(BlockProxy.HORIZONTAL_FACING)));
    }
}
