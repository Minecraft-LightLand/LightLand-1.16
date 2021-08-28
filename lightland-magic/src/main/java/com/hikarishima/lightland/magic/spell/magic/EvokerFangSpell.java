package com.hikarishima.lightland.magic.spell.magic;

import com.hikarishima.lightland.magic.registry.MagicEntityRegistry;
import com.hikarishima.lightland.magic.registry.entity.SpellEntity;
import com.hikarishima.lightland.magic.spell.internal.ActivationConfig;
import com.hikarishima.lightland.magic.spell.internal.SimpleSpell;
import com.hikarishima.lightland.magic.spell.internal.SpellConfig;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.world.World;

public class EvokerFangSpell extends SimpleSpell<EvokerFangSpell.Config> {

    @Override
    public int getDistance(PlayerEntity player) {
        return 0;
    }

    @Override
    public Config getConfig(World world, PlayerEntity player) {
        return SpellConfig.get(this, world, player);
    }

    @Override
    protected void activate(World world, PlayerEntity player, ActivationConfig activation, Config config) {
        if (world.isClientSide()) {
            return;
        }
        SpellEntity e = MagicEntityRegistry.ET_SPELL.create(world);
        if (e != null) {
            e.setData(player, config.spell_time, SpellEntity.SpellPlane.VERTICAL);
            world.addFreshEntity(e);
        }
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        for (int i = 0; i < config.layers.length; i++) {
            Layer layer = config.layers[i];
            for (int j = 0; j < layer.count; j++) {
                double angle = layer.angle * Math.PI / 180 + 2f * Math.PI * j / layer.count;
                double x0 = x + layer.radius * Math.cos(angle);
                double z0 = z + layer.radius * Math.sin(angle);
                world.addFreshEntity(new EvokerFangsEntity(world, x0, y, z0, (float) angle, layer.delay, player));
            }
        }
    }

    @SerialClass
    public static class Config extends SpellConfig {

        @SerialClass.SerialField
        public Layer[] layers;

        @SerialClass.SerialField
        public SpellDisplay spell_time;

    }

    @SerialClass
    public static class Layer {

        @SerialClass.SerialField
        public int count, delay;

        @SerialClass.SerialField
        public double angle, radius;

    }

}
