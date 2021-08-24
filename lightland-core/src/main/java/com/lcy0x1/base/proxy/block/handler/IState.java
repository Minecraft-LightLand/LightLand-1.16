package com.lcy0x1.base.proxy.block.handler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;

public interface IState extends IImpl {
    void createBlockStateDefinition(Block block, StateContainer.Builder<Block, BlockState> builder);
}