package com.lcy0x1.base.block;

import com.lcy0x1.base.block.type.IImpl;
import lombok.extern.log4j.Log4j2;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Log4j2
public class BaseBlock extends Block {
    private static final boolean defaultUseProxy = false;
    private static final boolean useProxy = Boolean.parseBoolean(System.getProperty("lightland.proxy.block", Boolean.toString(defaultUseProxy)));

    static {
        log.info("use proxy block: {}", System.getProperty("lightland.proxy.block"));
    }

    public static BaseBlock newBaseBlock(BlockProp p, IImpl... impl) {
        return useProxy ? ProxyBaseBlock.newBaseBlock(p, impl) : new ImplBaseBlock(p, impl);
    }

    protected BaseBlock(Properties props) {
        super(props);
    }

}