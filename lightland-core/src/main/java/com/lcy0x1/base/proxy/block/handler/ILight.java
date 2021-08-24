package com.lcy0x1.base.proxy.block.handler;


import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface ILight extends IImpl {

    int getLightValue(BlockState bs, IBlockReader w, BlockPos pos);

}