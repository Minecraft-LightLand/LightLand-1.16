package com.lcy0x1.base.block.mult;

import com.lcy0x1.base.block.type.IMultImpl;
import com.lcy0x1.base.proxy.ProxyContext;
import com.lcy0x1.base.proxy.Result;
import com.lcy0x1.base.proxy.annotation.WithinProxyContext;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;

@WithinProxyContext(block = true)
public interface IPlacement extends IMultImpl {
    ProxyContext.Key<Result<BlockState>> blockStateKey = new ProxyContext.Key<>(ProxyContext.pre);

    @SuppressWarnings("ConstantConditions")
    @WithinProxyContext(block = true, preSuper = true)
    default BlockState getStateForPlacement(BlockItemUseContext context) {
        final ProxyContext proxyContext = ProxyContext.local();
        Result<BlockState> blockState = proxyContext.get(blockStateKey);
        if (blockState == null || !blockState.isSuccess()) {
            blockState = Result.alloc(proxyContext.get(ProxyContext.block).defaultBlockState());
        }
        return getStateForPlacement(blockState.getResult(), context);
    }

    BlockState getStateForPlacement(BlockState def, BlockItemUseContext context);

}