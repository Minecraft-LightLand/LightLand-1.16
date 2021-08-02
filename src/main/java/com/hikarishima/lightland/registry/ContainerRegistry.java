package com.hikarishima.lightland.registry;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.gui.block.DisEnchanterContainer;
import com.hikarishima.lightland.magic.gui.block.DisEnchanterScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerRegistry {

    public static final ContainerType<DisEnchanterContainer> CT_DISENCH = getCT("disenchanter", DisEnchanterContainer::new);

    public static <T extends Container> ContainerType<T> getCT(String str, ContainerType.IFactory<T> fact) {
        ContainerType<T> ans = new ContainerType<>(fact);
        ans.setRegistryName(LightLand.MODID, str);
        return ans;
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerScreens() {
        ScreenManager.register(CT_DISENCH, DisEnchanterScreen::new);
    }

}
