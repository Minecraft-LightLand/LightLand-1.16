package com.hikarishima.lightland.proxy;

import com.hikarishima.lightland.magic.gui.MagicBookScreen;
import com.hikarishima.lightland.registry.ContainerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy implements ISidedProxy {

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
    public static void clientSetup(final FMLClientSetupEvent event) {
        ScreenManager.register(ContainerRegistry.CT_MAGIC_BOOK, MagicBookScreen::new);
    }

}
