package com.hikarishima.lightland.event.forge;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ItemUseEventHandler {

    public interface ItemClickHandler {

        boolean predicate(ItemStack stack, Class<? extends PlayerEvent> cls, PlayerEvent event);

        default void onPlayerLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {

        }

        default void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {

        }

        default void onPlayerLeftClickEntity(AttackEntityEvent event) {

        }

        default void onCriticalHit(CriticalHitEvent event) {

        }

        default void onPlayerRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {

        }

        default void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {

        }

        default void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteract event) {

        }

    }

    public static final List<ItemClickHandler> LIST = new ArrayList<>();

    public static <T extends PlayerEvent> void execute(ItemStack stack, T event, BiConsumer<ItemClickHandler, T> cons) {
        for (ItemClickHandler handler : LIST)
            if (handler.predicate(stack, event.getClass(), event))
                cons.accept(handler, event);
    }

    @SubscribeEvent
    public void onPlayerLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        execute(event.getItemStack(), event, ItemClickHandler::onPlayerLeftClickEmpty);
    }

    @SubscribeEvent
    public void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        execute(event.getItemStack(), event, ItemClickHandler::onPlayerLeftClickBlock);
    }

    @SubscribeEvent
    public void onPlayerLeftClickEntity(AttackEntityEvent event) {
        execute(event.getPlayer().getMainHandItem(), event, ItemClickHandler::onPlayerLeftClickEntity);
    }

    @SubscribeEvent
    public void onPlayerRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        execute(event.getItemStack(), event, ItemClickHandler::onPlayerRightClickEmpty);
    }

    @SubscribeEvent
    public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        execute(event.getItemStack(), event, ItemClickHandler::onPlayerRightClickBlock);
    }

    @SubscribeEvent
    public void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        execute(event.getItemStack(), event, ItemClickHandler::onPlayerRightClickEntity);
    }

    @SubscribeEvent
    public void onCriticalHit(CriticalHitEvent event) {
        execute(event.getPlayer().getMainHandItem(), event, ItemClickHandler::onCriticalHit);
    }

}
