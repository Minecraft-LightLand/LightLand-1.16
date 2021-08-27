package com.lcy0x1.base.block.mult;

import com.lcy0x1.base.block.type.IMultImpl;
import com.lcy0x1.base.proxy.ProxyContext;
import com.lcy0x1.base.proxy.annotation.WithinProxyContext;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;

@WithinProxyContext(block = true)
public interface IPlacement extends IMultImpl {
    ProxyContext.Key<BlockState> blockStateKey = new ProxyContext.Key<>();

    @SuppressWarnings("ConstantConditions")
    default BlockState getStateForPlacement(BlockItemUseContext context) {
        final ProxyContext proxyContext = ProxyContext.local();
        BlockState blockState = proxyContext.get(blockStateKey);
        if (blockState == null) {
            blockState = proxyContext.get(ProxyContext.block).defaultBlockState();
        }
        blockState = getStateForPlacement(blockState, context);
        proxyContext.put(blockStateKey, blockState);
        return blockState;
    }

    BlockState getStateForPlacement(BlockState def, BlockItemUseContext context);

}