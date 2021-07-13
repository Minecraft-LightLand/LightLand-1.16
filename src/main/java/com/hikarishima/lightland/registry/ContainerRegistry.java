package com.hikarishima.lightland.registry;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.gui.MagicBookContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class ContainerRegistry {

    public static ContainerType<MagicBookContainer> CT_MAGIC_BOOK =getCT("magic_book",MagicBookContainer::new);

    public static <T extends Container> ContainerType<T> getCT( String str, ContainerType.IFactory<T> fact) {
        ContainerType<T> ans = new ContainerType<>(fact);
        ans.setRegistryName(LightLand.MODID, str);
        return ans;
    }

}
