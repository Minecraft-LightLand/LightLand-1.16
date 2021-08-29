package com.lcy0x1.base.block.mult;

import com.lcy0x1.base.block.type.IMultImpl;
import com.lcy0x1.base.proxy.Proxy;
import com.lcy0x1.base.proxy.ProxyContext;
import com.lcy0x1.base.proxy.Reflections;
import com.lcy0x1.base.proxy.Result;
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
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public interface IClick extends IMultImpl {
    Logger log = LogManager.getLogger();
    Class<?>[] parameterTypes = {BlockState.class, World.class, BlockPos.class, PlayerEntity.class, Hand.class, BlockRayTraceResult.class};

    ActionResultType onClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockRayTraceResult r);

    @Override
    default Result<?> onProxy(@NotNull Proxy<?> obj, @NotNull Method method, @NotNull Object[] args, @NotNull MethodProxy proxy, @NotNull ProxyContext context) throws Throwable {
        final Result<?> result = Result.snapshot(IMultImpl.super.onProxy(obj, method, args, proxy, context));
        if (result.isSuccess() &&
                Reflections.equalsMethod(method, "onClick", parameterTypes) &&
                result.getResult() == ActionResultType.PASS) {
            log.warn("onProxy(obj: {}, method: {}, args: {}, proxy: {}, context: {}): {}",
                    obj, method, args, proxy, context, result);
            context.put(ProxyContext.continueFirstProxyMethod, true);
        }
        return result;
    }

}
