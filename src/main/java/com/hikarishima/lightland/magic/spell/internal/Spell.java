package com.hikarishima.lightland.magic.spell.internal;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class Spell<C extends SpellConfig, A extends ActivationConfig> extends AbstractSpell {

    public abstract A canActivate(World world, PlayerEntity player);

    public abstract C getConfig();

    public void activate(World world, PlayerEntity player, A activation) {
        MagicHandler magic = MagicHandler.get(player);
        C config = getConfig();
        magic.magicAbility.addSpellLoad(config.spell_load);
    }

}
