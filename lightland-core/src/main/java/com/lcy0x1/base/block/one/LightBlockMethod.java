package com.lcy0x1.base.block.one;


import com.lcy0x1.base.block.type.SingletonBlockMethod;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface LightBlockMethod extends SingletonBlockMethod {
    int getLightValue(BlockState bs, IBlockReader w, BlockPos pos);
}