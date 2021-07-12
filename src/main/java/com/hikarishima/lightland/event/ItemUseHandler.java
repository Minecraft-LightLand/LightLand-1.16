package com.hikarishima.lightland.event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ItemUseHandler {

    public interface ItemClickHandler {

        boolean predicate(ItemStack stack, Class<? extends PlayerEvent> cls, PlayerEvent event);

        default void onPlayerLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {

        }

        default void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {

        }

        default void onPlayerLeftClickEntity(AttackEntityEvent event) {

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

}
