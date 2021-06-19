package com.hikarishima.lightland.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface ISidedProxy {

    void init();
    PlayerEntity getPlayer();
    World getWorld();
    void openMagicBookGui();

}
