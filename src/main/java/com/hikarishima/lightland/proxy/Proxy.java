package com.hikarishima.lightland.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Supplier;

public class Proxy {

    @OnlyIn(Dist.CLIENT)
    public static ClientPlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static PlayerEntity getPlayer() {
        return DistExecutor.unsafeRunForDist(() -> Proxy::getClientPlayer, () -> () -> null);
    }

    public static <T> T getOnServer(Supplier<Supplier<T>> sup) {
        if (isServer())
            return sup.get().get();
        return null;
    }

    public static void runOnServer(Supplier<Runnable> sup) {
        if (isServer())
            sup.get().run();
    }

    public static boolean isServer() {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER)
            return true;
        return Minecraft.getInstance().isLocalServer();
    }

}
