package com.hikarishima.lightland.magic;

import com.hikarishima.lightland.magic.LightLandMagic;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        value = {Dist.CLIENT},
        modid = LightLandMagic.MODID
)
@SuppressWarnings("unused")
public class KeyHandler {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    public KeyHandler() {
    }

    public static void checkKeysPressed(int key) {
        //TODO
    }


    @SubscribeEvent
    public static void mouseEvent(InputEvent.MouseInputEvent event) {
        if (MINECRAFT.player != null && MINECRAFT.screen == null && event.getAction() == 1) {
            checkKeysPressed(event.getButton());
        }
    }

    @SubscribeEvent
    public static void keyEvent(InputEvent.KeyInputEvent event) {
        if (MINECRAFT.player != null && MINECRAFT.screen == null && event.getAction() == 1) {
            checkKeysPressed(event.getKey());
        }
    }

}
