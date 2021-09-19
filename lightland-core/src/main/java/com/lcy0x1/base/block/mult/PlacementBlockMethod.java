package com.lcy0x1.base.block.mult;

import com.lcy0x1.base.block.type.MultipleBlockMethod;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;

public interface PlacementBlockMethod extends MultipleBlockMethod {

    BlockState getStateForPlacement(BlockState def, BlockItemUseContext context);

}