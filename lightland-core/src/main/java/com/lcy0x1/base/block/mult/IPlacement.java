package com.lcy0x1.base.block.mult;

import com.lcy0x1.base.block.type.IMultImpl;
import com.lcy0x1.base.proxy.ProxyInterceptor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;

public interface IPlacement extends IMultImpl {

    default BlockState getStateForPlacement(BlockItemUseContext context) {
        return getStateForPlacement(ProxyInterceptor.getHandle((Block) null).defaultBlockState(), context);
    }

    BlockState getStateForPlacement(BlockState def, BlockItemUseContext context);

}