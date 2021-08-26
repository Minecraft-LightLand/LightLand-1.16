package com.hikarishima.lightland.magic.registry.entity;

import com.hikarishima.lightland.magic.registry.MagicEntityRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicFireBallEntity extends FireballEntity {

    public MagicFireBallEntity(EntityType<MagicFireBallEntity> type, World world) {
        super(type, world);
    }

    public MagicFireBallEntity(World world, LivingEntity owner, Vector3d vec) {
        this(MagicEntityRegistry.ET_FIRE_BALL, world);
        this.setOwner(owner);
        this.setPos(vec.x, vec.y, vec.z);
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
}
