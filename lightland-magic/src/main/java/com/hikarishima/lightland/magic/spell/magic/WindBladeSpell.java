package com.hikarishima.lightland.magic.spell.magic;

import com.hikarishima.lightland.magic.registry.MagicEntityRegistry;
import com.hikarishima.lightland.magic.registry.entity.SpellEntity;
import com.hikarishima.lightland.magic.registry.entity.WindBladeEntity;
import com.hikarishima.lightland.magic.spell.internal.ActivationConfig;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.hikarishima.lightland.magic.spell.internal.SpellConfig;
import com.lcy0x1.core.math.AutoAim;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class WindBladeSpell extends Spell<WindBladeSpell.Config, WindBladeSpell.Activation> {

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
            e.setData(player, config.spell_time, config.plane);
            e.setAction(spell -> {
                int t = spell.tickCount - config.spell_time.setup;
                if (t < 0 || t > config.spell_time.duration - config.spell_time.close)
                    return;
                if (t % config.period != 0)
                    return;
                Vector3d target = activation.target == null ? activation.pos :
                        activation.target.getPosition(1)
                                .add(0, activation.target.getBbHeight() / 2, 0);
                for (int offset : config.offset)
                    addBlade(config.normal, offset, world, spell, target, config);
            });
            world.addFreshEntity(e);
        }
    }

    private void addBlade(float noffset, float soffset, World world, SpellEntity spell, Vector3d target, Config config) {
        WindBladeEntity blade = MagicEntityRegistry.ET_WIND_BLADE.create(world);
        if (blade != null) {
            Vector3d pos = spell.getPosition(1);
            pos = AutoAim.getRayTerm(pos, spell.xRot, spell.yRot, noffset);
            pos = AutoAim.getRayTerm(pos, spell.xRot, spell.yRot + 90, soffset);
            blade.setPos(pos.x, pos.y, pos.z);
            Vector3d velocity = target.subtract(pos).normalize().scale(config.velocity);
            blade.setDeltaMovement(velocity);
            blade.setProperties(config.damage, Math.round(config.distance / config.velocity), 0f, ItemStack.EMPTY);
            world.addFreshEntity(blade);
        }
    }

    public static class Activation extends ActivationConfig {

        public Activation(World world, PlayerEntity player) {
            super(world, player, 64);
        }
    }

    @SerialClass
    public static class Config extends SpellConfig {

        @SerialClass.SerialField
        public int period, normal;

        @SerialClass.SerialField
        public int[] offset = {0};

        @SerialClass.SerialField
        public float damage, distance, velocity;

        @SerialClass.SerialField
        public SpellDisplay spell_time;

        @SerialClass.SerialField
        public SpellEntity.SpellPlane plane;

    }

}
