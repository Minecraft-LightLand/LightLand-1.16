package com.hikarishima.lightland.event.registry;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.recipe.RecipeRegistry;
import com.hikarishima.lightland.registry.ContainerRegistry;
import com.hikarishima.lightland.registry.ItemRegistry;
import com.hikarishima.lightland.registry.RegistryBase;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GenericRegistryEvents {

    @SubscribeEvent
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        MagicRegistry.createRegistries();
    }

    @SubscribeEvent
    public static void onItemRegistry(RegistryEvent.Register<Item> event) {
        RegistryBase.process(ItemRegistry.class, Item.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
        RegistryBase.process(ItemRegistry.class, Block.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onTileEntityTypeRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
        RegistryBase.process(ContainerRegistry.class, TileEntityType.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onContainerTypeRegistry(RegistryEvent.Register<ContainerType<?>> event) {
        RegistryBase.process(ContainerRegistry.class, ContainerType.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onRecipeSerializerRegistry(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        RegistryBase.process(RecipeRegistry.class, IRecipeSerializer.class, event.getRegistry()::register);
    }

}
