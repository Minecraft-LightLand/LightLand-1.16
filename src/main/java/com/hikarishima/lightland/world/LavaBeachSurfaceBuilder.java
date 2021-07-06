package com.hikarishima.lightland.world;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.util.Random;

public class LavaBeachSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {

    public LavaBeachSurfaceBuilder(Codec<SurfaceBuilderConfig> config) {
        super(config);
    }

    @Override
    public void apply(Random random, IChunk chunk, Biome biome,
                      int x, int z, int y, double v,
                      BlockState b0, BlockState b1,
                      int top, long l, SurfaceBuilderConfig config) {

        BlockPos.Mutable pos = new BlockPos.Mutable();
        int depth = -1;
        int thickness = (int) (v / 3d + 7d + random.nextDouble() * 0.25D);
        int cx = x & 15;
        int cz = z & 15;
        for (int iy = y; iy > 0; --iy) {
            pos.set(cx, iy, cz);
            if (chunk.getBlockState(pos) != b0)
                continue;
            depth++;
            if (depth > thickness)
                return;
            BlockState bs = random.nextDouble() < 0.5 ? config.getUnderwaterMaterial():config.getTopMaterial();
            chunk.setBlockState(pos, bs, false);
        }
    }

}
