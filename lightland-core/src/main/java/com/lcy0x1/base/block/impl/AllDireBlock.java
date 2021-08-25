package com.lcy0x1.base.block.impl;

import com.lcy0x1.base.block.BlockProxy;
import com.lcy0x1.base.block.mult.IPlacement;
import com.lcy0x1.base.block.mult.IState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;

public class AllDireBlock implements IPlacement, IState {

    public AllDireBlock() {
    }

    @Override
    public void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockProxy.FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockState def, BlockItemUseContext context) {
        return def.setValue(BlockProxy.FACING, context.getClickedFace().getOpposite());
    }
}
