package com.lcy0x1.base;

import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@SerialClass
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BaseEntity extends Entity implements IEntityAdditionalSpawnData {

    public BaseEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT tag) {
        tag.put("auto-serial", Automator.toTag(new CompoundNBT(), this));
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT tag) {
        if (!tag.contains("auto-serial"))
            return;
        ExceptionHandler.run(() -> Automator.fromTag(tag.getCompound("auto-serial"), this.getClass(), this, f -> true));
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        Serializer.to(buffer, this);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void readSpawnData(PacketBuffer data) {
        Serializer.from(data, (Class) this.getClass(), this);
    }

}
