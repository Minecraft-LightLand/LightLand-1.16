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

public class LavaSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {

    public LavaSurfaceBuilder(Codec<SurfaceBuilderConfig> config) {
        super(config);
    }

    @Override
    public void apply(Random random, IChunk chunk, Biome biome,
                      int x, int z, int y, double v,
                      BlockState b0, BlockState b1,
                      int top, long l, SurfaceBuilderConfig config) {

        BlockPos.Mutable pos = new BlockPos.Mutable();
        int depth = -1;
        int thickness = (int)(v / 3d + 3d + random.nextDouble() * 0.25D);
        int cx = x & 15;
        int cz = z & 15;

        for(int iy = y;iy>100;--iy){
            pos.set(cx,iy,cz);
            if(iy>111)
                chunk.setBlockState(pos,Blocks.AIR.defaultBlockState(),false);
            else chunk.setBlockState(pos,config.getTopMaterial().getBlockState(),false);
        }

    }


    public void applyx(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
        this.apply(p_205610_1_, p_205610_2_, p_205610_3_, p_205610_4_, p_205610_5_, p_205610_6_, p_205610_7_, p_205610_9_, p_205610_10_, p_205610_14_.getTopMaterial(), p_205610_14_.getUnderMaterial(), p_205610_14_.getUnderwaterMaterial(), p_205610_11_);
    }

    protected void apply(Random r, IChunk c, Biome b, int x, int z, int y, double v, BlockState b0, BlockState b1, BlockState c0, BlockState c1, BlockState c2, int top) {
        BlockState ic0 = c0;
        BlockState ic1 = c1;
        BlockPos.Mutable pos = new BlockPos.Mutable();
        int depth = -1;
        int thickness = (int)(v / 3.0D + 3.0D + r.nextDouble() * 0.25D);
        int cx = x & 15;
        int cy = z & 15;

        for(int iy = y; iy >= 0; --iy) {
            pos.set(cx, iy, cy);
            BlockState current = c.getBlockState(pos);
            if (current.isAir()) {
                depth = -1;
            } else if (current.is(b0.getBlock())) {
                if (depth == -1) {
                    if (thickness <= 0) {
                        ic0 = Blocks.AIR.defaultBlockState();
                        ic1 = b0;
                    } else if (iy >= top - 4 && iy <= top + 1) {
                        ic0 = c0;
                        ic1 = c1;
                    }

                    if (iy < top && (ic0 == null || ic0.isAir())) {
                        if (b.getTemperature(pos.set(x, iy, z)) < 0.15F) {
                            ic0 = Blocks.ICE.defaultBlockState();
                        } else {
                            ic0 = b1;
                        }

                        pos.set(cx, iy, cy);
                    }

                    depth = thickness;
                    if (iy >= top - 1) {
                        c.setBlockState(pos, ic0, false);
                    } else if (iy < top - 7 - thickness) {
                        ic0 = Blocks.AIR.defaultBlockState();
                        ic1 = b0;
                        c.setBlockState(pos, c2, false);
                    } else {
                        c.setBlockState(pos, ic1, false);
                    }
                } else if (depth > 0) {
                    --depth;
                    c.setBlockState(pos, ic1, false);
                    if (depth == 0 && ic1.is(Blocks.SAND) && thickness > 1) {
                        depth = r.nextInt(4) + Math.max(0, iy - 63);
                        ic1 = ic1.is(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState();
                    }
                }
            }
        }

    }
}
