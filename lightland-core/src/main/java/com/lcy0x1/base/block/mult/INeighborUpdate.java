package com.lcy0x1.base.block.mult;

import com.lcy0x1.base.block.type.IMultImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface INeighborUpdate extends IMultImpl {

    void neighborChanged(Block self, BlockState state, World world, BlockPos pos, Block nei_block, BlockPos nei_pos, boolean moving);

}
