package com.hikarishima.lightland.magic.registry.entity;

import com.hikarishima.lightland.magic.arcane.ArcaneRegistry;
import com.hikarishima.lightland.magic.registry.MagicEntityRegistry;
import com.hikarishima.lightland.magic.registry.item.combat.IArcaneWeapon;
import com.lcy0x1.core.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WindBladeEntity extends ThrowableEntity implements IEntityAdditionalSpawnData {

    @SerialClass.SerialField
    public float damage = 3;
    @SerialClass.SerialField
    public int last = 200;
    @SerialClass.SerialField
    public boolean isArcane = false;
    @SerialClass.SerialField
    public float zrot = 0f;

    private ItemStack issuer;

    public WindBladeEntity(EntityType<? extends WindBladeEntity> type, World w) {
        super(type, w);
    }

    public WindBladeEntity(World w) {
        this(MagicEntityRegistry.ET_WIND_BLADE.get(), w);
    }

    @Override
    protected void defineSynchedData() {
    }

    public void setProperties(float damage, int last, float zrot, ItemStack issuer) {
        this.damage = damage;
        this.last = last;
        this.zrot = zrot;
        this.isArcane = !issuer.isEmpty();
        this.issuer = issuer;

        Vector3d vector3d = this.getDeltaMovement();
        float f = MathHelper.sqrt(getHorizontalDistanceSqr(vector3d));
        this.xRot = (float) (MathHelper.atan2(vector3d.y, f) * (double) (180F / (float) Math.PI));
        this.yRot = (float) (MathHelper.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI));
        this.xRotO = this.xRot;
        this.yRotO = this.yRot;
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

        double vx = velocity.x;
        double vy = velocity.y;
        double vz = velocity.z;
        for (int i = 0; i < 4; ++i) {
            this.level.addParticle(ParticleTypes.CRIT,
                    this.getX() + vx * (double) i / 4.0D,
                    this.getY() + vy * (double) i / 4.0D,
                    this.getZ() + vz * (double) i / 4.0D,
                    -vx, -vy + 0.2, -vz);
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
            DamageSource source = new IndirectEntityDamageSource("wind_blade", entity, owner);
            if (isArcane) {
                source = IArcaneWeapon.toMagic(issuer, this, owner, source, damage, ArcaneRegistry.ARCANE_TIME);
            }
            entity.hurt(source, damage);
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
