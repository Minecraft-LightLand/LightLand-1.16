package com.lcy0x1.base.block.one;

import com.lcy0x1.base.block.type.IOneImpl;
import com.lcy0x1.base.proxy.ProxyInterceptor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;

public interface IFace extends IOneImpl {

    default BlockState getStateForPlacement(BlockItemUseContext context) {
        return getStateForPlacement(ProxyInterceptor.getHandle((Block) null).defaultBlockState(), context);
    }

    BlockState getStateForPlacement(BlockState def, BlockItemUseContext context);

}