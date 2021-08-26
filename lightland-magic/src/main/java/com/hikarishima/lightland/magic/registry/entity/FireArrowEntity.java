package com.hikarishima.lightland.magic.registry.entity;

import com.hikarishima.lightland.magic.registry.MagicEntityRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FireArrowEntity extends ArrowEntity {

    public FireArrowEntity(EntityType<FireArrowEntity> type, World world) {
        super(type, world);
    }

    public FireArrowEntity(World world, LivingEntity owner) {
        this(MagicEntityRegistry.ET_FIRE_ARROW, world);
        this.setOwner(owner);
    }

    @Override
    protected void tickDespawn() {
        for (int i = 0; i < 6; i++) {
            if (this.isAlive())
                super.tickDespawn();
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
