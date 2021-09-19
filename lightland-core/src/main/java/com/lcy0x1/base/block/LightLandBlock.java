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

    public static LightLandBlock newBaseBlock(LightLandBlockProperties p, BlockMethod... impl) {
        return new LightLandBlockImpl(p, impl);
    }

    protected LightLandBlock(Properties props) {
        super(props);
    }

}