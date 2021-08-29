package com.lcy0x1.base.block;

import com.lcy0x1.base.block.type.BlockMethod;
import lombok.extern.log4j.Log4j2;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Log4j2
public class LightLandBlock extends Block {
    private static final boolean defaultUseProxy = true;
    private static final boolean useProxy = Boolean.parseBoolean(System.getProperty("lightland.proxy.block", Boolean.toString(defaultUseProxy)));

    static {
        log.info("use proxy block: {}", useProxy);
    }

    public static LightLandBlock newBaseBlock(BlockProp p, BlockMethod... impl) {
        return useProxy ? ProxyLightLandBlockImpl.newBaseBlock(p, impl) : new LightLandBlockImpl(p, impl);
    }

    protected LightLandBlock(Properties props) {
        super(props);
    }

}