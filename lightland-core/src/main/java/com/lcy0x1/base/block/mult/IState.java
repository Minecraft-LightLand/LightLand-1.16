package com.lcy0x1.base.block.mult;

import com.lcy0x1.base.block.type.IMultImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;

public interface IState extends IMultImpl {

    void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder);

}