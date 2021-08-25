package com.lcy0x1.base.block.mult;

import com.lcy0x1.base.block.type.IMultImpl;
import com.lcy0x1.base.proxy.Proxy;
import com.lcy0x1.base.proxy.ProxyInterceptor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

public interface IClick extends IMultImpl {
    Logger log = LogManager.getLogger();
    Class<?>[] parameterTypes = {BlockState.class, World.class, BlockPos.class, PlayerEntity.class, Hand.class, BlockRayTraceResult.class};

    //default ActionResultType use(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockRayTraceResult r) {
    //    return onClick(bs, w, pos, pl, h, r);
    //}

    ActionResultType onClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockRayTraceResult r);

    @Override
    default Proxy.Result<?> onProxy(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        final Proxy.Result<?> result = IMultImpl.super.onProxy(obj, method, args, proxy);
        log.warn("onProxy: {} {} {}", method, result, ProxyInterceptor.equalsMethod(method, "onClick", parameterTypes));
        if (result != null && result.isSuccess() && ProxyInterceptor.equalsMethod(method, "onClick", parameterTypes)) {
            if (result.getResult() == ActionResultType.PASS) {
                return Proxy.failed();
            }
        }
        return result;
    }

}
