package com.hikarishima.lightland.registry;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.gui.MagicBookContainer;
import com.hikarishima.lightland.magic.gui.MagicBookScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;

public class ContainerRegistry {

    public static ContainerType<MagicBookContainer> CT_MAGIC_BOOK = getCT("magic_book", MagicBookContainer::new);

    public static <T extends Container> ContainerType<T> getCT(String str, ContainerType.IFactory<T> fact) {
        ContainerType<T> ans = new ContainerType<>(fact);
        ans.setRegistryName(LightLand.MODID, str);
        return ans;
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerScreens() {
        LogManager.getLogger().info("start registering screens");
        ScreenManager.register(CT_MAGIC_BOOK, MagicBookScreen::new);
    }

}
