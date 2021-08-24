package com.hikarishima.lightland.magic.registry.block;

import com.lcy0x1.base.BaseBlock;
import com.lcy0x1.core.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SerialClass
public class RitualTE extends SyncedSingleItemTE {


    public RitualTE(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dire) {
        return dire == Direction.UP && super.canPlaceItemThroughFace(slot, stack, dire);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dire) {
        return dire == Direction.DOWN && super.canTakeItemThroughFace(slot, stack, dire);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public void activate(){

    }

    public static class RitualPlace implements BaseBlock.IClick {

        @Override
        public ActionResultType onClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockRayTraceResult r) {
            TileEntity te = w.getBlockEntity(pos);
            if (te instanceof RitualTE) {
                RitualTE rte = (RitualTE) te;
                if (rte.isEmpty()) {
                    if (pl.getMainHandItem().isEmpty()) {
                        return ActionResultType.PASS;
                    } else {
                        if (!w.isClientSide()) {
                            rte.setItem(0, pl.getMainHandItem().split(1));
                        }
                        return ActionResultType.SUCCESS;
                    }
                } else {
                    if (pl.getMainHandItem().isEmpty()) {
                        if (!w.isClientSide()) {
                            pl.inventory.placeItemBackInInventory(w, rte.removeItem(0, 1));
                        }
                    } else {
                        rte.activate();
                    }
                    return ActionResultType.SUCCESS;
                }
            }
            return ActionResultType.PASS;
        }
    }

}
