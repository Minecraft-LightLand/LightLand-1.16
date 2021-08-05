package com.hikarishima.lightland.proxy;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class Proxy {

    @OnlyIn(Dist.CLIENT)
    public static ClientPlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static PlayerEntity getPlayer() {
        return DistExecutor.unsafeRunForDist(() -> Proxy::getClientPlayer, () -> () -> null);
    }

    public static World getWorld() {
        return DistExecutor.unsafeRunForDist(() -> Proxy::getClientWorld, () -> Proxy::getServerWorld);
    }

    @OnlyIn(Dist.CLIENT)
    public static ClientWorld getClientWorld() {
        return Minecraft.getInstance().level;
    }

    public static ServerWorld getServerWorld() {
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        return server.overworld();
    }

    public static int getMargin(PlayerEntity player) {
        if (player.level.isClientSide())
            return 0;
        return MagicHandler.get(player).magicAbility.getManaRestoration() * 5;
    }
}
