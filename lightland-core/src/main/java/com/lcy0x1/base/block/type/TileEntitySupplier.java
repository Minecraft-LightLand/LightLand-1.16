package com.lcy0x1.base.block.type;

import net.minecraft.tileentity.TileEntity;

import java.util.function.Supplier;

public interface TileEntitySupplier extends BlockMethod, Supplier<TileEntity> {
    @Override
    TileEntity get();
}