package com.lcy0x1.base.block.mult;

import com.lcy0x1.base.block.type.IMultImpl;
import com.lcy0x1.base.proxy.ProxyContext;
import com.lcy0x1.base.proxy.annotation.WithinProxyContext;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;

@WithinProxyContext(block = true)
public interface IPlacement extends IMultImpl {
    @SuppressWarnings("ConstantConditions")
    default BlockState getStateForPlacement(BlockItemUseContext context) {
        return getStateForPlacement(ProxyContext.local().get(ProxyContext.block)
            .defaultBlockState(), context);
    }

    BlockState getStateForPlacement(BlockState def, BlockItemUseContext context);

}