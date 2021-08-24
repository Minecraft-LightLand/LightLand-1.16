package com.hikarishima.lightland.magic.registry.block;

import com.hikarishima.lightland.magic.registry.MagicContainerRegistry;
import com.lcy0x1.base.BaseBlock;
import com.lcy0x1.base.BlockProp;
import com.lcy0x1.base.proxy.block.STE;
import com.lcy0x1.core.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RitualSide extends BaseBlock {

    public RitualSide(BlockProp p) {
        super(p, RitualCore.CLICK, (STE) RitualCore.TE::new);
    }

    @SerialClass
    public static class TE extends RitualTE {

        public TE() {
            super(MagicContainerRegistry.TE_RITUAL_CORE);
        }

    }

}
