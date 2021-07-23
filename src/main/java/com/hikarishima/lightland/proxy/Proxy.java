package com.hikarishima.lightland.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

public class Proxy {

    @OnlyIn(Dist.CLIENT)
    public static ClientPlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static PlayerEntity getPlayer() {
        return DistExecutor.unsafeRunForDist(() -> Proxy::getClientPlayer, () -> () -> null);
    }

}
