package com.hikarishima.lightland.magic.spell.magic;

import com.hikarishima.lightland.magic.spell.internal.ActivationConfig;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.hikarishima.lightland.magic.spell.internal.SpellConfig;
import com.hikarishima.lightland.registry.block.TempBlock;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

public class DirtWallSpell extends Spell<DirtWallSpell.Config, DirtWallSpell.Activation> {

    @Override
    public Activation canActivate(Type type, World world, PlayerEntity player) {
        return new Activation(player, world);
    }

    @Override
    public Config getConfig(PlayerEntity player, Activation activation) {
        return SpellConfig.get(this, activation);
    }

    @Override
    public void activate(World world, PlayerEntity player, Activation act, Config config) {
        if (world.isClientSide())
            return;
        ServerWorld w = (ServerWorld) world;
        Block b = ForgeRegistries.BLOCKS.getValue(config.block);
        if (b == null)
            return;
        BlockState state = b.defaultBlockState();
        BlockPos pos = new BlockPos(act.pos);
        Direction dk = Direction.fromYRot(player.yRot);
        Direction dj = Direction.fromYRot(player.yRot + 90);
        Direction di = Direction.UP;
        BlockPos.Mutable mpos = new BlockPos.Mutable();
        for (int i = -config.ri; i <= config.ri; i++)
            for (int j = -config.rj; j <= config.rj; j++)
                for (int k = -config.rk; k <= config.rk; k++) {
                    mpos.set(pos);
                    mpos.move(di, i);
                    mpos.move(dj, j);
                    mpos.move(dk, k);
                    TempBlock.putBlock(w, mpos, state, config.time);
                }
    }

    @SerialClass
    public static class Config extends SpellConfig {

        @SerialClass.SerialField
        public ResourceLocation block;

        @SerialClass.SerialField
        public int time, ri, rj, rk;

    }

    public static class Activation extends ActivationConfig {

        public Activation(PlayerEntity player, World world) {
            super(world, player, 4);
        }
    }

}
