package com.lcy0x1.base.block.one;

import com.lcy0x1.base.block.type.IOneImpl;
import com.lcy0x1.base.proxy.annotation.Singleton;
import net.minecraft.block.BlockState;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

@Singleton
public interface IRotMir extends IOneImpl {
    BlockState mirror(BlockState state, Mirror mirrorIn);

    BlockState rotate(BlockState state, Rotation rot);
}