package com.hikarishima.lightland.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy implements ISidedProxy{

    @Override
    public PlayerEntity getPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public World getWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientProxy::clientSetup);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup(final FMLClientSetupEvent event){

    }

    @Override
    public void openMagicBookGui() {
        Minecraft.getInstance().setScreen(null);//FIXME
    }

}
