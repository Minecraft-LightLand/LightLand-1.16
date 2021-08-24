package com.lcy0x1.base.block.one;

import com.lcy0x1.base.block.type.IOneImpl;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;

public interface IFace extends IOneImpl {
    BlockState getStateForPlacement(BlockState def, BlockItemUseContext context);
}