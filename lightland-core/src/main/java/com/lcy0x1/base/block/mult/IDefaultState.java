package com.lcy0x1.base.block.mult;

import com.lcy0x1.base.block.type.IMultImpl;
import net.minecraft.block.BlockState;

public interface IDefaultState extends IMultImpl {

    BlockState getDefaultState(BlockState state);

}
