package com.lcy0x1.base.block.type;

import com.lcy0x1.base.block.type.IImpl;
import net.minecraft.tileentity.TileEntity;

import java.util.function.Supplier;

public interface STE extends IImpl, Supplier<TileEntity> {
    @Override
    TileEntity get();
}