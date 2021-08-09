package com.hikarishima.lightland.registry;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.gui.container.*;
import com.hikarishima.lightland.magic.gui.container.ChemContainer;
import com.hikarishima.lightland.magic.gui.container.ChemScreen;
import com.hikarishima.lightland.magic.gui.container.experimental.MagicCraftContainer;
import com.hikarishima.lightland.magic.gui.container.experimental.MagicCraftScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerRegistry {

    public static final ContainerType<DisEnchanterContainer> CT_DISENCH = getCT("disenchant", DisEnchanterContainer::new);
    public static final ContainerType<MagicCraftContainer> CT_MAGIC_CRAFT = getCT("magic_craft", MagicCraftContainer::new);
    public static final ContainerType<SpellCraftContainer> CT_SPELL_CRAFT = getCT("spell_craft", SpellCraftContainer::new);
    public static final ContainerType<ArcaneInjectContainer> CT_ARCANE_INJECT = getCT("arcane_inject", ArcaneInjectContainer::new);
    public static final ContainerType<ChemContainer> CT_CHEM = getCT("chemistry", ChemContainer::new);

    public static <T extends Container> ContainerType<T> getCT(String str, ContainerType.IFactory<T> fact) {
        ContainerType<T> ans = new ContainerType<>(fact);
        ans.setRegistryName(LightLand.MODID, str);
        return ans;
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerScreens() {
        ScreenManager.register(CT_DISENCH, DisEnchanterScreen::new);
        ScreenManager.register(CT_MAGIC_CRAFT, MagicCraftScreen::new);
        ScreenManager.register(CT_SPELL_CRAFT, SpellCraftScreen::new);
        ScreenManager.register(CT_ARCANE_INJECT, ArcaneInjectScreen::new);
        ScreenManager.register(CT_CHEM, ChemScreen::new);
    }

}
