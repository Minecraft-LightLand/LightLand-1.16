package com.hikarishima.lightland.registry;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.gui.DisEnchantContainer;
import com.hikarishima.lightland.magic.gui.DisEnchantScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerRegistry {

    public static ContainerType<DisEnchantContainer> CT_MAGIC_BOOK = getCT("magic_book", DisEnchantContainer::new);

    public static <T extends Container> ContainerType<T> getCT(String str, ContainerType.IFactory<T> fact) {
        ContainerType<T> ans = new ContainerType<>(fact);
        ans.setRegistryName(LightLand.MODID, str);
        return ans;
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerScreens() {
        ScreenManager.register(CT_MAGIC_BOOK, DisEnchantScreen::new);
    }

}
