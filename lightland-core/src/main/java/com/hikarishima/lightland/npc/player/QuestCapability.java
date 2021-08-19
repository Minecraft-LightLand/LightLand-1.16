package com.hikarishima.lightland.npc.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class QuestCapability implements ICapabilitySerializable<CompoundNBT> {

    public final PlayerEntity player;
    public final World w;
    public QuestHandler handler = new QuestHandler();
    public LazyOptional<QuestHandler> lo = LazyOptional.of(() -> this.handler);

    public QuestCapability(PlayerEntity player, World w) {
        this.player = player;
        this.w = w;
        if (w == null)
            LogManager.getLogger().error("world not present in entity");
        handler.world = w;
        handler.player = player;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction direction) {
        if (capability == QuestHandler.CAPABILITY)
            return lo.cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) QuestHandler.STORAGE.writeNBT(QuestHandler.CAPABILITY, lo.resolve().get(), null);
    }

    @Override
    public void deserializeNBT(CompoundNBT compoundNBT) {
        QuestHandler.STORAGE.readNBT(QuestHandler.CAPABILITY, handler, null, compoundNBT);
        handler.init();
    }

}
