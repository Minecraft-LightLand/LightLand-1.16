package com.hikarishima.lightland.magic;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface IMagicHandler {

    @Nullable
    static IMagicHandler get(PlayerEntity e) {
        return IMagicHandlerFactory.factory.getIMagicHandler(e);
    }

    IAbilityPoints getAbilityPoints();
}
