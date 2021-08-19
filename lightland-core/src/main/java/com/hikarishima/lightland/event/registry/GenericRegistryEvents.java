package com.hikarishima.lightland.event.registry;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.recipe.RecipeRegistry;
import com.hikarishima.lightland.registry.ContainerRegistry;
import com.hikarishima.lightland.registry.ItemRegistry;
import com.hikarishima.lightland.registry.RegistryBase;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@SuppressWarnings("unused")
public class GenericRegistryEvents {

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        MagicRegistry.createRegistries();
        RegistryBase.process(MagicRegistry.class, IForgeRegistry.class, GenericRegistryEvents::regSerializer);
    }

    private static <T extends IForgeRegistryEntry<T>> void regSerializer(IForgeRegistry<T> r) {
        new Serializer.RLClassHandler<>(r.getRegistrySuperType(), () -> r);
        new Automator.RegistryClassHandler<>(r.getRegistrySuperType(), () -> r);
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
