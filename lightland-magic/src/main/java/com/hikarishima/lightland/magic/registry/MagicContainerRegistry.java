package com.hikarishima.lightland.magic.registry;

import com.google.common.collect.Sets;
import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.gui.container.*;
import com.hikarishima.lightland.magic.registry.block.RitualCore;
import com.hikarishima.lightland.magic.registry.block.RitualRenderer;
import com.hikarishima.lightland.magic.registry.block.RitualSide;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class MagicContainerRegistry {

    public static final DeferredRegister<TileEntityType<?>> TE = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, LightLandMagic.MODID);
    public static final DeferredRegister<ContainerType<?>> CT = DeferredRegister.create(ForgeRegistries.CONTAINERS, LightLandMagic.MODID);

    public static final RegistryObject<TileEntityType<RitualCore.TE>> TE_RITUAL_CORE = getTE("ritual_core", RitualCore.TE::new, MagicItemRegistry.B_RITUAL_CORE);
    public static final RegistryObject<TileEntityType<RitualSide.TE>> TE_RITUAL_SIDE = getTE("ritual_side", RitualSide.TE::new, MagicItemRegistry.B_RITUAL_SIDE);

    public static final RegistryObject<ContainerType<DisEnchanterContainer>> CT_DISENCH = getCT("disenchant", DisEnchanterContainer::new);
    public static final RegistryObject<ContainerType<SpellCraftContainer>> CT_SPELL_CRAFT = getCT("spell_craft", SpellCraftContainer::new);
    public static final RegistryObject<ContainerType<ArcaneInjectContainer>> CT_ARCANE_INJECT = getCT("arcane_inject", ArcaneInjectContainer::new);
    public static final RegistryObject<ContainerType<ChemContainer>> CT_CHEM = getCT("chemistry", ChemContainer::new);

    @SuppressWarnings("ConstantConditions")
    public static <T extends TileEntity> RegistryObject<TileEntityType<T>> getTE(String str, Supplier<T> sup, RegistryObject<? extends Block> blocks) {
        return TE.register(str, () -> new TileEntityType<>(sup, Sets.newHashSet(blocks.get()), null));
    }

    public static <T extends Container> RegistryObject<ContainerType<T>> getCT(String str, ContainerType.IFactory<T> fact) {
        return CT.register(str, () -> new ContainerType<>(fact));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerScreens() {
        ScreenManager.register(CT_DISENCH.get(), DisEnchanterScreen::new);
        ScreenManager.register(CT_SPELL_CRAFT.get(), SpellCraftScreen::new);
        ScreenManager.register(CT_ARCANE_INJECT.get(), ArcaneInjectScreen::new);
        ScreenManager.register(CT_CHEM.get(), ChemScreen::new);
        ClientRegistry.bindTileEntityRenderer(TE_RITUAL_CORE.get(), RitualRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TE_RITUAL_SIDE.get(), RitualRenderer::new);
    }

}
