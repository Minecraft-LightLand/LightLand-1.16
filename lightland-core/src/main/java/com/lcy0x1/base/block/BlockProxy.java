package com.lcy0x1.base.block;

import com.lcy0x1.base.block.impl.AllDireBlock;
import com.lcy0x1.base.block.impl.HorizontalBlock;
import com.lcy0x1.base.block.impl.Power;
import com.lcy0x1.base.block.impl.TriggerBlock;
import com.lcy0x1.base.block.type.IImpl;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;

public class BlockProxy {

    public static final IImpl POWER = new Power();
    public static final IImpl ALL_DIRECTION = new AllDireBlock();
    public static final IImpl HORIZONTAL = new HorizontalBlock();
    public static final IImpl TRIGGER = new TriggerBlock(4);

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

}
