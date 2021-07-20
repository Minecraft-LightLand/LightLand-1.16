package com.hikarishima.lightland.magic.spell.magic;

import com.hikarishima.lightland.magic.spell.internal.ActivationConfig;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.hikarishima.lightland.magic.spell.internal.SpellConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class DirtWallSpell extends Spell<DirtWallSpell.Config, DirtWallSpell.Activation> {

    @Override
    public Activation canActivate(Type type, World world, PlayerEntity player) {
        return null;
    }

    @Override
    public Config getConfig(PlayerEntity player, Activation activation) {
        return null;
    }

    @Override
    public void activate(World world, PlayerEntity player, Activation activation) {

    }

    public static class Config extends SpellConfig {

    }

    public static class Activation extends ActivationConfig {

    }

}
