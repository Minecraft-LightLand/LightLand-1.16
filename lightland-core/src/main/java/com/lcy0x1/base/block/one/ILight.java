package com.lcy0x1.base.block.one;


import com.lcy0x1.base.block.type.IOneImpl;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface ILight extends IOneImpl {
    int getLightValue(BlockState bs, IBlockReader w, BlockPos pos);
}