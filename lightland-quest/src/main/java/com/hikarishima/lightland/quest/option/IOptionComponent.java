package com.hikarishima.lightland.quest.option;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IOptionComponent {

    @OnlyIn(Dist.CLIENT)
    default boolean test(PlayerEntity player) {
        return true;
    }

    /**
     * when implemented, be aware of the server-client side difference
     */
    default void perform(PlayerEntity player) {

    }

}
