package com.lcy0x1.base.block.mult;

import com.lcy0x1.base.block.type.IImpl;
import com.lcy0x1.base.block.type.IMultImpl;
import com.lcy0x1.base.proxy.Proxy;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public interface IClick extends IMultImpl {

    ActionResultType onClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockRayTraceResult r);

    @Override
    default Proxy.Result<?> onProxy(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        final Proxy.Result<?> result = IMultImpl.super.onProxy(obj, method, args, proxy);
        if (result != null && result.isSuccess() && result.getResult() instanceof ActionResultType) {
            if (result.getResult() == ActionResultType.PASS) {
                return Proxy.failed();
            }
        }
        return result;
    }

}
