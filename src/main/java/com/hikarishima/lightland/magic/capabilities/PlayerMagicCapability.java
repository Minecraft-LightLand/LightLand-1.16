package com.hikarishima.lightland.magic.capabilities;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SerialClass
public class PlayerMagicCapability implements ICapabilitySerializable<CompoundNBT> {

    public final World w;
    public MagicHandler handler = new MagicHandler();
    public LazyOptional<MagicHandler> lo = LazyOptional.of(() -> this.handler);

    public PlayerMagicCapability(World w) {
        this.w = w;
        if (w == null)
            LogManager.getLogger().error("world not present in entity");
        handler.world = w;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction direction) {
        if (capability == MagicHandler.CAPABILITY)
            return lo.cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) MagicHandler.STORAGE.writeNBT(MagicHandler.CAPABILITY, lo.resolve().get(), null);
    }

    @Override
    public void deserializeNBT(CompoundNBT compoundNBT) {
        MagicHandler.STORAGE.readNBT(MagicHandler.CAPABILITY, handler, null, compoundNBT);
        handler.init();
    }

}
