package com.hikarishima.lightland.magic.spell.internal;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class Spell<C extends SpellConfig, A extends ActivationConfig> extends AbstractSpell {

    public abstract A canActivate(Type type, World world, PlayerEntity player);

    public abstract C getConfig(PlayerEntity player, A activation);

    public abstract void activate(World world, PlayerEntity player, A activation, C config);

    public boolean attempt(Type type, World world, PlayerEntity player) {
        A a = canActivate(type, world, player);
        if (a == null)
            return false;
        C c = getConfig(player, a);
        MagicHandler handler = MagicHandler.get(player);
        if (type == Type.WAND) {
            if (c.mana_cost > handler.magicAbility.getMana()) {
                return false;
            }
            handler.magicAbility.giveMana(-c.mana_cost);
        } else {
            handler.magicAbility.addSpellLoad(c.spell_load);
        }
        activate(world, player, a, c);
        return true;
    }

    public enum Type {
        SCROLL, WAND
    }

}
