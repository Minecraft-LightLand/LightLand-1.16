package com.hikarishima.lightland.magic.spell.magic;

import com.hikarishima.lightland.magic.registry.block.TempBlock;
import com.hikarishima.lightland.magic.spell.internal.ActivationConfig;
import com.hikarishima.lightland.magic.spell.internal.SimpleSpell;
import com.hikarishima.lightland.magic.spell.internal.SpellConfig;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DirtWallSpell extends SimpleSpell<DirtWallSpell.Config> {

    @Override
    public Config getConfig(World world, PlayerEntity player) {
        return SpellConfig.get(this, world, player);
    }

    @Override
    protected void activate(World world, PlayerEntity player, ActivationConfig act, Config config) {
        if (world.isClientSide())
            return;
        ServerWorld w = (ServerWorld) world;
        if (config.block == null)
            return;
        BlockState state = config.block.defaultBlockState();
        BlockPos pos = new BlockPos(act.pos);
        Direction dn = Direction.fromYRot(player.yRot);
        Direction ds = Direction.fromYRot(player.yRot + 90);
        Direction dy = Direction.UP;
        BlockPos.Mutable mpos = new BlockPos.Mutable();
        for (int y = -config.ry; y <= config.ry; y++)
            for (int s = -config.rs; s <= config.rs; s++)
                for (int n = -config.rn; n <= config.rn; n++) {
                    mpos.set(pos);
                    mpos.move(dy, y);
                    mpos.move(ds, s);
                    mpos.move(dn, n);
                    TempBlock.putBlock(w, mpos, state, config.time);
                }
    }

    @Override
    public int getDistance(PlayerEntity player) {
        return 4;
    }

    @SerialClass
    public static class Config extends SpellConfig {

        @SerialClass.SerialField
        public Block block;

        @SerialClass.SerialField
        public int time, ry, rs, rn;

    }

}
