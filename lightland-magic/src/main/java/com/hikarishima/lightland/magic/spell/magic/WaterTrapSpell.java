package com.hikarishima.lightland.magic.spell.magic;

import com.hikarishima.lightland.magic.registry.MagicEntityRegistry;
import com.hikarishima.lightland.magic.registry.VanillaMagicRegistry;
import com.hikarishima.lightland.magic.registry.entity.SpellEntity;
import com.hikarishima.lightland.magic.spell.internal.ActivationConfig;
import com.hikarishima.lightland.magic.spell.internal.SimpleSpell;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.hikarishima.lightland.magic.spell.internal.SpellConfig;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.LightLandFakeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class WaterTrapSpell extends SimpleSpell<WaterTrapSpell.Config> {

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
        SpellEntity e = MagicEntityRegistry.ET_SPELL.create(world);
        if (e != null) {
            e.setData(activation, config.spell_time);
            e.setAction(spell -> {
                int t = spell.tickCount - config.spell_time.setup;
                if (t != 0)
                    return;
                world.getEntities(player, new AxisAlignedBB(spell.blockPosition()).inflate(config.radius),
                        le -> le instanceof LivingEntity &&
                                !le.isAlliedTo(player) &&
                                le.position().distanceTo(spell.position()) < config.radius
                ).forEach(le -> LightLandFakeEntity.addEffect((LivingEntity) le,
                        new EffectInstance(VanillaMagicRegistry.WATER_TRAP, config.effect_time, config.effect_level)));
            });
            world.addFreshEntity(e);
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
        public int effect_time, effect_level;

        @SerialClass.SerialField
        public float radius;

        @SerialClass.SerialField
        public SpellDisplay spell_time;

    }

}
