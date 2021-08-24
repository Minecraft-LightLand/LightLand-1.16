package com.lcy0x1.base.proxy.block.handler;

import net.minecraft.block.BlockState;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public interface IRotMir extends IImpl {
    BlockState mirror(BlockState state, Mirror mirrorIn);

    BlockState rotate(BlockState state, Rotation rot);
}