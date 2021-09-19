package com.lcy0x1.base.block.one;

import com.lcy0x1.base.block.type.SingletonBlockMethod;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public interface TitleEntityBlockMethod extends SingletonBlockMethod {
    default boolean hasTileEntity(BlockState state) {
        return true;
    }

    TileEntity createTileEntity(BlockState state, IBlockReader world);
}