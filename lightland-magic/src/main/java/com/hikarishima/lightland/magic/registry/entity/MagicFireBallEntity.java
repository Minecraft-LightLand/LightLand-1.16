package com.hikarishima.lightland.magic.registry.entity;

import com.hikarishima.lightland.magic.registry.MagicEntityRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicFireBallEntity extends FireballEntity implements IEntityAdditionalSpawnData, ISizedItemEntity {

    public MagicFireBallEntity(EntityType<MagicFireBallEntity> type, World world) {
        super(type, world);
    }

    private float size;

    public MagicFireBallEntity(World world, LivingEntity owner, Vector3d vec, float size) {
        this(MagicEntityRegistry.ET_FIRE_BALL.get(), world);
        this.setOwner(owner);
        this.setPos(vec.x, vec.y, vec.z);
        this.size = size;
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (!this.level.isClientSide) {
            this.level.explode(this, getX(), getY(), getZ(), explosionPower, false, Explosion.Mode.NONE);
            this.remove();
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeFloat(size);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        size = additionalData.readFloat();
    }

    @Override
    public float getSize() {
        return 3 * (1 + size * 2);
    }

    public EntitySize getDimensions(Pose pose) {
        return EntitySize.scalable(1 + size * 2, 1 + size * 2);
    }

}
