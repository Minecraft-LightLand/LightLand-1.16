package com.lcy0x1.base.block.type;

import com.lcy0x1.base.block.impl.TitleEntityBlockMethodImpl;
import com.lcy0x1.base.proxy.container.MutableProxyContainer;
import com.lcy0x1.base.proxy.handler.ProxyMethod;
import net.minecraft.tileentity.TileEntity;

import java.util.function.Supplier;

public interface TileEntitySupplier extends BlockMethod, Supplier<TileEntity> {
    @Override
    default boolean onAdded(MutableProxyContainer<ProxyMethod> container) {
        container.addProxy(new TitleEntityBlockMethodImpl(this));
        return false;
    }

    @Override
    TileEntity get();
}