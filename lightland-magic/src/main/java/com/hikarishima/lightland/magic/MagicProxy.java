package com.hikarishima.lightland.magic;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.chem.HashEquationPool;
import com.hikarishima.lightland.proxy.Proxy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MagicProxy {

    @OnlyIn(Dist.CLIENT)
    public static MagicHandler getHandler() {
        return MagicHandler.get(Proxy.getClientPlayer());
    }

    @OnlyIn(Dist.CLIENT)
    public static HashEquationPool getPool() {
        return HashEquationPool.getPool(Proxy.getClientWorld());
    }

    public static int getMargin(PlayerEntity player) {
        if (player.level.isClientSide())
            return 0;
        return MagicHandler.get(player).magicAbility.getManaRestoration() * 5;
    }

}
