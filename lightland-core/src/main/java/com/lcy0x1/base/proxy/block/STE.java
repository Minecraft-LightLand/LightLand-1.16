package com.lcy0x1.base.proxy.block;

import net.minecraft.tileentity.TileEntity;

import java.util.function.Supplier;

public interface STE extends IImpl, Supplier<TileEntity> {
    @Override
    TileEntity get();
}