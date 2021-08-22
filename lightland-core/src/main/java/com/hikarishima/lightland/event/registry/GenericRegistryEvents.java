package com.hikarishima.lightland.event.registry;

import com.hikarishima.lightland.registry.RegistryBase;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@SuppressWarnings("unused")
public class GenericRegistryEvents {


    @SubscribeEvent
    public static void onItemRegistry(RegistryEvent.Register<Item> event) {
        for (Class<?> cls : RegistryBase.REGISTRIES)
            RegistryBase.process(cls, Item.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
        for (Class<?> cls : RegistryBase.REGISTRIES)
            RegistryBase.process(cls, Block.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onTileEntityTypeRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
        for (Class<?> cls : RegistryBase.REGISTRIES)
            RegistryBase.process(cls, TileEntityType.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onContainerTypeRegistry(RegistryEvent.Register<ContainerType<?>> event) {
        for (Class<?> cls : RegistryBase.REGISTRIES)
            RegistryBase.process(cls, ContainerType.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onRecipeSerializerRegistry(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        for (Class<?> cls : RegistryBase.REGISTRIES)
            RegistryBase.process(cls, IRecipeSerializer.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onEffectRegistry(RegistryEvent.Register<Effect> event){
        for (Class<?> cls : RegistryBase.REGISTRIES)
            RegistryBase.process(cls, Effect.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onEnchantmentRegistry(RegistryEvent.Register<Enchantment> event){
        for (Class<?> cls : RegistryBase.REGISTRIES)
            RegistryBase.process(cls, Enchantment.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onEntityTypeRegistry(RegistryEvent.Register<EntityType<?>> event){
        for (Class<?> cls : RegistryBase.REGISTRIES)
            RegistryBase.process(cls, EntityType.class, event.getRegistry()::register);
    }

}
