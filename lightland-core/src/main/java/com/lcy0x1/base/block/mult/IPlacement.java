package com.lcy0x1.base.block.mult;

import cn.tursom.proxy.ProxyInterceptor;
import com.lcy0x1.base.block.type.IMultImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;

public interface IPlacement extends IMultImpl {

    default BlockState getStateForPlacement(BlockItemUseContext context) {
        return getStateForPlacement(((Block) ProxyInterceptor.Companion.getHandle()).defaultBlockState(), context);
    }

    BlockState getStateForPlacement(BlockState def, BlockItemUseContext context);

}