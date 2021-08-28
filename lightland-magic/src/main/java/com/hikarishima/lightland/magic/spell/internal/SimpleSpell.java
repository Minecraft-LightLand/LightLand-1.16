package com.hikarishima.lightland.magic.spell.internal;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class SimpleSpell<C extends SpellConfig> extends Spell<C, ActivationConfig> {

    @Override
    protected final ActivationConfig canActivate(Type type, World world, PlayerEntity player) {
        return new ActivationConfig(world, player, getDistance(player));
    }

}
