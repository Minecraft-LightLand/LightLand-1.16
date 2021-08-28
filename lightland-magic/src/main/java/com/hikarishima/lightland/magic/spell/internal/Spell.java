package com.hikarishima.lightland.magic.spell.internal;

import com.hikarishima.lightland.magic.MagicProxy;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.ToClientMsg;
import com.hikarishima.lightland.proxy.PacketHandler;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

public abstract class Spell<C extends SpellConfig, A extends ActivationConfig> extends AbstractSpell {

    protected abstract A canActivate(Type type, World world, PlayerEntity player);

    public abstract C getConfig(World world, PlayerEntity player);

    protected abstract void activate(World world, PlayerEntity player, A activation, C config);

    public boolean attempt(Type type, World world, PlayerEntity player) {
        boolean ans = inner_attempt(type, world, player);
        if (!world.isClientSide()) {
            ServerPlayerEntity e = (ServerPlayerEntity) player;
            PacketHandler.toClient(e, new ToClientMsg(ToClientMsg.Action.MAGIC_ABILITY, MagicHandler.get(e)));
        }
        return ans;
    }

    private boolean inner_attempt(Type type, World world, PlayerEntity player) {
        A a = canActivate(type, world, player);
        if (a == null)
            return false;
        C c = getConfig(player.level, player);
        MagicHandler handler = MagicHandler.get(player);
        if (type == Type.WAND) {
            int margin = MagicProxy.getMargin(player);
            if (c.mana_cost - margin > handler.magicAbility.getMana()) {
                return false;
            }
            handler.magicAbility.giveMana(-c.mana_cost);
        } else {
            handler.magicAbility.addSpellLoad(c.spell_load);
        }
        activate(world, player, a, c);
        return true;
    }

    public abstract int getDistance(PlayerEntity player);

    public enum Type {
        SCROLL, WAND
    }

}
