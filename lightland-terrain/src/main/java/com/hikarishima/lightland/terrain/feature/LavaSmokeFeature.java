package com.hikarishima.lightland.terrain.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import org.apache.logging.log4j.LogManager;

import java.util.Random;

public class LavaSmokeFeature extends Feature<LavaSmokeFeatureConfig> {

    public static final BlockState AIR = Blocks.AIR.defaultBlockState();
    public static final BlockState CAMPFIRE = Blocks.CAMPFIRE.defaultBlockState();
    public static final BlockState HAYBALE = Blocks.HAY_BLOCK.defaultBlockState();
    public static final BlockState BASE = Blocks.BASALT.defaultBlockState();
    public static final BlockState LAVA = Blocks.LAVA.defaultBlockState();

    private static final float c1 = 0.8f, c2 = 0.8f, c3 = 0.8f;
    private static final int DEPTH = 2, MAX_DEPTH = 14;

    public LavaSmokeFeature(Codec<LavaSmokeFeatureConfig> codec) {
        super(codec);
    }

    private static BlockState getTopBlock(int dx, int dz, float r) {
        if (!inCircle(dx, dz, r))
            return null;
        if (inCircle(dx - 1, dz, r) && inCircle(dx + 1, dz, r) && inCircle(dx, dz - 1, r) && inCircle(dx, dz + 1, r))
            return LAVA;
        return BASE;
    }

    private static boolean inCircle(int dx, int dz, float r) {
        return (dx - 0.5f) * (dx - 0.5f) + (dz - 0.5f) * (dz - 0.5f) < r * r;
    }

    @Override
    public boolean place(
            ISeedReader w, ChunkGenerator gen, Random random,
            BlockPos pos, LavaSmokeFeatureConfig config) {
        float f0 = random.nextFloat() - 0.5f;
        float f1 = -f0 * (1 - c1) + (random.nextFloat() - 0.5f) * c1;
        float f2 = f0 * (1 - c2) + (random.nextFloat() - 0.5f) * c2;
        float f3 = f0 * (1 - c3) + (random.nextFloat() - 0.5f) * c3;
        float size = config.size + config.delta_size * f1;
        int height = Math.round(config.size + config.delta_height * f2);
        float slope = config.slope + config.delta_slope * f3;
        int dy = 0;
        BlockPos.Mutable mpos = new BlockPos.Mutable();
        for (int y = pos.getY() + height; y >= 0; y--) {
            boolean set = false;
            for (int dx = (int) -Math.ceil(size); dx <= Math.ceil(size); dx++) {
                for (int dz = (int) -Math.ceil(size); dz <= Math.ceil(size); dz++) {
                    if (inCircle(dx, dz, size)) {
                        mpos.set(pos.getX() + dx, y, pos.getZ() + dz);
                        if (dy == 0) {
                            BlockState bs = getTopBlock(dx, dz, size);
                            if (bs != null)
                                w.setBlock(mpos, bs, 3);
                            if (bs == LAVA) {
                                while (!w.getBlockState(mpos.move(Direction.UP)).isAir()) {
                                    w.setBlock(mpos, AIR, 3);
                                }
                            }
                        } else {
                            BlockState bs = w.getBlockState(mpos.above());
                            BlockState self = w.getBlockState(mpos);
                            if (bs.isAir())
                                bs = null;
                            if (bs == LAVA || bs == CAMPFIRE || bs == HAYBALE) {
                                if (dy == DEPTH)
                                    bs = CAMPFIRE;
                                else if (dy == DEPTH + 1)
                                    bs = HAYBALE;
                                if (dy > DEPTH + 1 || dy >= DEPTH && !inCircle(dx, dz, 1))
                                    bs = null;
                            }
                            if (bs == null && (self == LAVA || self.isAir()))
                                bs = BASE;
                            if (bs != null && bs != self) {
                                set = true;
                                w.setBlock(mpos, bs, 3);
                                while (w.getBlockState(mpos.move(Direction.DOWN)).isAir()) {
                                    w.setBlock(mpos, BASE, 3);
                                }
                            }
                        }
                    }
                }
            }
            dy++;
            size += 1f / slope;
            if (dy > height + 1 && !set)
                return true;
            if (dy > MAX_DEPTH) {
                LogManager.getLogger().error("incomplete lava well generation at " + pos);
                return true;
            }
        }
        return true;
    }
}
