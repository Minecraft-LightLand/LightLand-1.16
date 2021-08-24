package com.lcy0x1.base.proxy.block.handler;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;

public interface IFace extends IImpl {
    BlockState getStateForPlacement(BlockState def, BlockItemUseContext context);
}