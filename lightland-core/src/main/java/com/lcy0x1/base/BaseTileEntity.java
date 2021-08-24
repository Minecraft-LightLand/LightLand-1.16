package com.lcy0x1.base;

import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SerialClass
public class BaseTileEntity extends TileEntity {

    public BaseTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        if (tag.contains("auto-serial"))
            ExceptionHandler.run(() -> Automator.fromTag(tag.getCompound("auto-serial"), getClass(), this, f -> true));
        super.load(state, tag);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT ser = ExceptionHandler.get(() -> Automator.toTag(new CompoundNBT(), getClass(), this, f -> true));
        if (ser != null) tag.put("auto-serial", ser);
        return super.save(tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT ser = ExceptionHandler.get(() -> Automator.toTag(new CompoundNBT(), getClass(), this, SerialClass.SerialField::toClient));
        if (ser != null) return new SUpdateTileEntityPacket(getBlockPos(), -1, ser);
        return super.getUpdatePacket();
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        ExceptionHandler.run(() -> Automator.fromTag(pkt.getTag(), getClass(), this, SerialClass.SerialField::toClient));
        super.onDataPacket(net, pkt);
    }

    public void sync() {
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT ans = super.getUpdateTag();
        CompoundNBT ser = ExceptionHandler.get(() -> Automator.toTag(new CompoundNBT(), getClass(), this, f -> true));
        if (ser != null) ans.put("auto-serial", ser);
        return ans;
    }

}
