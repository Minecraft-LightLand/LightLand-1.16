package com.lcy0x1.base.block;

import com.lcy0x1.base.block.type.IImpl;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BaseBlock extends Block {

    private static final boolean useProxy = false;

    public static BaseBlock newBaseBlock(BlockProp p, IImpl... impl) {
        return useProxy ? ProxyBaseBlock.newBaseBlock(p, impl) : new ImplBaseBlock(p, impl);
    }

    protected BaseBlock(Properties props) {
        super(props);
    }

}