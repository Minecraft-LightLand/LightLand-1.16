package com.hikarishima.lightland.magic.registry;

import com.google.common.collect.Sets;
import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.gui.container.*;
import com.hikarishima.lightland.magic.registry.block.RitualCore;
import com.hikarishima.lightland.magic.registry.block.RitualSide;
import com.hikarishima.lightland.magic.registry.block.RitualRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.function.Supplier;

public class MagicContainerRegistry {

    public static final TileEntityType<RitualCore.TE> TE_RITUAL_CORE = getTE("ritual_core", RitualCore.TE::new, MagicItemRegistry.B_RITUAL_CORE);
    public static final TileEntityType<RitualSide.TE> TE_RITUAL_SIDE = getTE("ritual_side", RitualSide.TE::new, MagicItemRegistry.B_RITUAL_SIDE);

    public static final ContainerType<DisEnchanterContainer> CT_DISENCH = getCT("disenchant", DisEnchanterContainer::new);
    public static final ContainerType<SpellCraftContainer> CT_SPELL_CRAFT = getCT("spell_craft", SpellCraftContainer::new);
    public static final ContainerType<ArcaneInjectContainer> CT_ARCANE_INJECT = getCT("arcane_inject", ArcaneInjectContainer::new);
    public static final ContainerType<ChemContainer> CT_CHEM = getCT("chemistry", ChemContainer::new);

    public static <T extends TileEntity> TileEntityType<T> getTE(String str, Supplier<T> sup, Block... blocks) {
        TileEntityType<T> ans = new TileEntityType<>(sup, Sets.newHashSet(blocks), null);
        ans.setRegistryName(LightLandMagic.MODID, str);
        return ans;
    }

    public static <T extends Container> ContainerType<T> getCT(String str, ContainerType.IFactory<T> fact) {
        ContainerType<T> ans = new ContainerType<>(fact);
        ans.setRegistryName(LightLandMagic.MODID, str);
        return ans;
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerScreens() {
        ScreenManager.register(CT_DISENCH, DisEnchanterScreen::new);
        ScreenManager.register(CT_SPELL_CRAFT, SpellCraftScreen::new);
        ScreenManager.register(CT_ARCANE_INJECT, ArcaneInjectScreen::new);
        ScreenManager.register(CT_CHEM, ChemScreen::new);
        ClientRegistry.bindTileEntityRenderer(TE_RITUAL_CORE, RitualRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TE_RITUAL_SIDE, RitualRenderer::new);
    }

}
