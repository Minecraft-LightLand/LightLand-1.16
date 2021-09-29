package com.hikarishima.lightland.magic;

import com.hikarishima.lightland.magic.LightLandMagic;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = LightLandMagic.MODID,
        value = {Dist.CLIENT},
        bus = Mod.EventBusSubscriber.Bus.MOD
)
@SuppressWarnings("unused")
public class KeyBinder {

    private static final String CATEGORY = "key.category.lightland-magic.general";
    public static final KeyBinding[] BINDS = new KeyBinding[4];
    private static final int[] KEYS = {82, 84, 98, 85};

    static {
        for (int i = 0; i < BINDS.length; i++) {
            BINDS[i] = new KeyBinding("key.lightland-magic.skill_" + i, KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, KEYS[i], CATEGORY);
        }
    }

    @SubscribeEvent
    public static void registerKeyBindings(FMLClientSetupEvent event) {
        for (KeyBinding key : BINDS) {
            ClientRegistry.registerKeyBinding(key);
        }
    }

}
