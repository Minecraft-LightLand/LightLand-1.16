package com.hikarishima.lightland.magic.capabilities;

import com.hikarishima.lightland.magic.IMagicHandler;
import com.hikarishima.lightland.magic.IMagicHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;

public class MagicHandlerFactory implements IMagicHandlerFactory {
    @Override
    public IMagicHandler getIMagicHandler(PlayerEntity e) {
        return MagicHandler.get(e);
    }
}
