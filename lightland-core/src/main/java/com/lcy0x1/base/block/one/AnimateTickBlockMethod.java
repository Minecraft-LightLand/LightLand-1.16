package com.lcy0x1.base.block.one;

import com.lcy0x1.base.block.type.MultipleBlockMethod;
import com.lcy0x1.base.proxy.annotation.Singleton;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@Singleton
public interface AnimateTickBlockMethod extends MultipleBlockMethod {

    @OnlyIn(Dist.CLIENT)
    void animateTick(BlockState state, World world, BlockPos pos, Random r);

}
