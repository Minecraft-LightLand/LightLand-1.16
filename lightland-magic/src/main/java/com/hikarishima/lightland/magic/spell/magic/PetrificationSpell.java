package com.hikarishima.lightland.magic.spell.magic;

import com.hikarishima.lightland.magic.registry.VanillaMagicRegistry;
import com.hikarishima.lightland.magic.registry.entity.misc.SpellEntity;
import com.hikarishima.lightland.magic.spell.internal.ActivationConfig;
import com.hikarishima.lightland.magic.spell.internal.SimpleSpell;
import com.hikarishima.lightland.magic.spell.internal.SpellConfig;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class PetrificationSpell extends SimpleSpell<PetrificationSpell.Config> {

    @Override
    public Config getConfig(World world, PlayerEntity player) {
        return SpellConfig.get(this, world, player);
    }

    @Override
    public int getDistance(PlayerEntity player) {
        return 64;
    }

    @Override
    protected void activate(World world, PlayerEntity player, ActivationConfig activation, Config config) {
        if (world.isClientSide()) {
            return;
        }
        SpellEntity e = new SpellEntity(world);
        e.setData(activation, config.spell_time);
        e.setAction(spell -> {
            int t = spell.tickCount - config.spell_time.setup;
            if (t != 0)
                return;
            world.getEntities(player, new AxisAlignedBB(spell.blockPosition()).inflate(config.radius),
                    le -> le instanceof LivingEntity &&
                            !le.isAlliedTo(player) &&
                            le.position().distanceTo(spell.position()) < config.radius
            ).forEach(le -> {
                LivingEntity liv = (LivingEntity) le;
                int time = config.effect_time;
                int lv = config.effect_level;
                if (liv.getHealth() > config.max_hp)
                    return;
                liv.addEffect(new EffectInstance(Effects.REGENERATION, time, 0));
                liv.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, time, lv));
                liv.addEffect(new EffectInstance(VanillaMagicRegistry.EFF_PETRI.get(), time, lv));
            });
        });
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
        public int effect_time, effect_level;

        @SerialClass.SerialField
        public float radius, max_hp;

        @SerialClass.SerialField
        public SpellDisplay spell_time;

    }

}
