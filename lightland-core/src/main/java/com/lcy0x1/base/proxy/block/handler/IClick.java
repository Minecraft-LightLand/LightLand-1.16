package com.lcy0x1.base.proxy.block.handler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public interface IClick extends IImpl {

    ActionResultType onClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockRayTraceResult r);

}
