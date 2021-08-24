package com.lcy0x1.base.proxy.block.handler;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public interface ITE extends IImpl {

    TileEntity createTileEntity(BlockState state, IBlockReader world);

}