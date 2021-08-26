package com.hikarishima.lightland.magic.spell.magic;

import com.hikarishima.lightland.magic.registry.MagicEntityRegistry;
import com.hikarishima.lightland.magic.registry.entity.FireArrowEntity;
import com.hikarishima.lightland.magic.registry.entity.MagicFireBallEntity;
import com.hikarishima.lightland.magic.registry.entity.SpellEntity;
import com.hikarishima.lightland.magic.spell.internal.ActivationConfig;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.hikarishima.lightland.magic.spell.internal.SpellConfig;
import com.lcy0x1.core.math.AutoAim;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class FireArrowSpell extends Spell<FireArrowSpell.Config, FireArrowSpell.Activation> {

    @Override
    protected Activation canActivate(Type type, World world, PlayerEntity player) {
        return new Activation(world, player);
    }

    @Override
    public Config getConfig(World world, PlayerEntity player) {
        return SpellConfig.get(this, world, player);
    }

    @Override
    protected void activate(World world, PlayerEntity player, Activation activation, Config config) {
        if (world.isClientSide()) {
            return;
        }
        SpellEntity e = MagicEntityRegistry.ET_SPELL.create(world);
        if (e != null) {
            e.setData(activation, config.spell_time);
            e.setAction(spell -> {
                int t = spell.tickCount - config.spell_time.setup;
                if (t < 0 || t > config.spell_time.duration - config.spell_time.close)
                    return;
                if (t % config.period != 0)
                    return;
                for (int i = 0; i < config.repeat; i++) {
                    Vector3d target = activation.pos;
                    float angle = (float) (Math.random() * 360);
                    float radius = (float) (Math.random() * config.radius);
                    target = AutoAim.getRayTerm(target, 0, angle, radius);
                    if (config.explosion == 0) {
                        addArrow(target, player, world, config);
                    } else {
                        addFireball(target, player, world, config);
                    }
                }
            });
            world.addFreshEntity(e);
        }
    }

    private void addArrow(Vector3d target, PlayerEntity player, World world, Config config) {
        AbstractArrowEntity e = new FireArrowEntity(world, player);
        e.pickup = AbstractArrowEntity.PickupStatus.DISALLOWED;
        e.setSecondsOnFire(100);
        Vector3d pos = target.add(0, config.distance, 0);
        e.setPos(pos.x, pos.y, pos.z);
        Vector3d velocity = new Vector3d(0, -config.velocity, 0);
        e.setDeltaMovement(velocity);
        e.setCritArrow(true);
        e.setBaseDamage(config.damage);

        world.addFreshEntity(e);
    }

    private void addFireball(Vector3d target, PlayerEntity player, World world, Config config) {
        Vector3d pos = target.add(0, config.distance, 0);
        MagicFireBallEntity e = new MagicFireBallEntity(world, player, pos);
        Vector3d velocity = new Vector3d(0, -config.velocity, 0);
        e.setDeltaMovement(velocity);
        e.explosionPower = config.explosion;
        world.addFreshEntity(e);
    }

    public static class Activation extends ActivationConfig {

        public Activation(World world, PlayerEntity player) {
            super(world, player, 64);
        }
    }

    @SerialClass
    public static class Config extends SpellConfig {

        @SerialClass.SerialField
        public int period, repeat, explosion;

        @SerialClass.SerialField
        public float damage, distance, velocity, radius;

        @SerialClass.SerialField
        public SpellDisplay spell_time;

    }

}
