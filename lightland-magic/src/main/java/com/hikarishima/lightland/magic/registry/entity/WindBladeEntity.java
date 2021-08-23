package com.hikarishima.lightland.magic.registry.entity;

import com.hikarishima.lightland.magic.registry.item.combat.IArcaneWeapon;
import com.lcy0x1.core.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WindBladeEntity extends ThrowableEntity implements IEntityAdditionalSpawnData {

    private float damage = 3;
    private int last = 200;
    public float zrot = 0f;

    public WindBladeEntity(EntityType<? extends WindBladeEntity> type, World w) {
        super(type, w);
    }

    @Override
    protected void defineSynchedData() {
    }

    public void setProperties(float damage, int last, float zrot) {
        this.damage = damage;
        this.last = last;
        this.zrot = zrot;
    }

    @Override
    public void tick() {
        Vector3d velocity = getDeltaMovement();
        super.tick();
        this.setDeltaMovement(velocity);
        last--;
        if (last <= 0) {
            remove();
        }
    }

    protected float getGravity() {
        return 0;
    }

    protected void onHit(RayTraceResult result) {
        super.onHit(result);
        if (!level.isClientSide) {
            remove();
        }
    }

    protected void onHitEntity(EntityRayTraceResult result) {
        super.onHitEntity(result);
        if (!level.isClientSide) {
            Entity entity = result.getEntity();
            Entity owner = this.getOwner();
            entity.hurt(IArcaneWeapon.toMagic(this, owner, DamageSource.indirectMagic(this, owner), damage, 200), damage);
            if (owner instanceof LivingEntity) {
                doEnchantDamageEffects((LivingEntity) owner, entity);
            }
            remove();
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public float getZRot() {
        return zrot;
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeFloat(zrot);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        zrot = additionalData.readFloat();
    }
}