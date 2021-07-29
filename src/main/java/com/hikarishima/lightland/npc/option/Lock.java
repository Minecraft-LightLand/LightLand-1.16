package com.hikarishima.lightland.npc.option;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;

@SerialClass
public class Lock implements IOptionComponent {

    public boolean test(PlayerEntity player) {
        //TODO
        return false;
    }

}
