package com.hikarishima.lightland.magic;

import lombok.Data;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ServiceLoader;

public interface IMagicHandlerFactory {
    Factory factory = new Factory();

    @Data
    class Factory {
        private IMagicHandlerFactory factory;

        IMagicHandler getIMagicHandler(PlayerEntity e) {
            if (factory != null) {
                return factory.getIMagicHandler(e);
            } else {
                return null;
            }
        }
    }

    IMagicHandler getIMagicHandler(PlayerEntity e);
}
