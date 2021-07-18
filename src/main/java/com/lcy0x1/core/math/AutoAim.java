package com.lcy0x1.core.math;

import com.lcy0x1.core.math.Estimator.EstiResult;
import com.lcy0x1.core.math.Estimator.EstiType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;

import java.util.Optional;
import java.util.function.Predicate;

public class AutoAim {

    public static Entity getEntity(ShootConfig config) {
        config.velo = MathHelper.clamp(config.pullProgress, 0f, 1f) * config.maxVelo;
        if (config.ammo.getItem() == Items.TNT)
            return getTNTEntity(config);
        return getProcessedPE(config);
    }

    private static AbstractArrowEntity getAbstractArrowEntity(ShootConfig config) {
        if (config.ppe != null)
            return config.ppe;
        if (config.ammo.getItem() instanceof ArrowItem) {
            return ((ArrowItem) config.ammo.getItem()).createArrow(config.world, config.ammo, config.player);
        }
        if (config.ammo.getItem() == Items.TRIDENT) {
            return new TridentEntity(config.world, config.player, config.ammo);
        }
        return null;
    }

    private static ProjectileEntity getProcessedPE(ShootConfig config) {
        ProjectileEntity e = getProjectileEntity(config);
        EstiResult er = setAim(config.player, config.velo, config.r, e, config.g, config.k, config.t);
        if (er.getType() == EstiType.ZERO)
            e.setDeltaMovement(er.getVec());
        else
            e.shootFromRotation(config.player, config.player.xRot, config.player.yRot, 0, config.velo, 1);
        return e;
    }

    private static ProjectileEntity getProcessedPPE(ShootConfig config) {
        AbstractArrowEntity entity = getAbstractArrowEntity(config);
        if (entity == null)
            return null;
        if (config.pullProgress == 1.0F)
            entity.setCritArrow(true);
        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, config.bow);
        if (j > 0)
            entity.setBaseDamage(entity.getBaseDamage() + (j + 1) * config.power);
        int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, config.bow);
        if (k > 0)
            entity.setKnockback(k * config.punch);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, config.bow) > 0)
            entity.setRemainingFireTicks(config.firetime);
        if (config.omitConsume || config.player.isCreative())
            entity.pickup = AbstractArrowEntity.PickupStatus.DISALLOWED;
        return entity;
    }

    private static ProjectileEntity getProjectileEntity(ShootConfig config) {
        config.setData(0.05, 0.01, 128, 120);
        if (config.ammo.getItem() instanceof ThrowablePotionItem) {
            PotionEntity p = new PotionEntity(config.world, config.player);
            p.setItem(config.ammo);
            return p;
        }
        config.setData(0.03, 0.01, 128, 120);
        if (config.pe != null)
            return config.pe;
        if (config.ammo.getItem() == Items.ENDER_PEARL)
            return new EnderPearlEntity(config.world, config.player);
        if (config.ammo.getItem() == Items.SNOWBALL)
            return new SnowballEntity(config.world, config.player);
        config.setData(0.05, 0.01, 128, 120);
        return getProcessedPPE(config);
    }

    private static Vector3d getRayTerm(Vector3d pos, float pitch, float yaw, double reach) {
        float f2 = MathHelper.cos(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = MathHelper.sin(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -MathHelper.cos(-pitch * ((float) Math.PI / 180F));
        float f5 = MathHelper.sin(-pitch * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        return pos.add(f6 * reach, f5 * reach, f7 * reach);
    }

    private static Entity getTNTEntity(ShootConfig config) {
        TNTEntity e = new TNTEntity(config.world, config.player.getX(), config.player.getEyeY() - 0.1,
                config.player.getZ(), config.player);
        EstiResult er = setAim(config.player, config.velo, 128, e, 0.04, 0.02, 80);
        if (er.getType() == EstiType.ZERO) {
            e.setDeltaMovement(er.getVec());
            e.setFuse((int) Math.round(er.getT()));
        } else if (er.getType() == EstiType.FAIL)
            setDire(config.player, config.velo, e);
        else
            return null;
        return e;
    }

    private static BlockRayTraceResult rayTraceBlock(World worldIn, PlayerEntity player, double reach) {
        float f = player.xRot;
        float f1 = player.yRot;
        Vector3d Vector3d = new Vector3d(player.getX(), player.getEyeY(), player.getZ());
        Vector3d Vector3d1 = getRayTerm(Vector3d, f, f1, reach);
        return worldIn.clip(new RayTraceContext(Vector3d, Vector3d1, RayTraceContext.BlockMode.OUTLINE,
                RayTraceContext.FluidMode.NONE, player));
    }

    public static EntityRayTraceResult rayTraceEntity(PlayerEntity player, double reach, Predicate<Entity> pred) {
        World world = player.level;
        Vector3d pos = new Vector3d(player.getX(), player.getEyeY(), player.getZ());
        Vector3d end = getRayTerm(pos, player.xRot, player.yRot, reach);
        AxisAlignedBB box = new AxisAlignedBB(pos, end).inflate(1);
        double d0 = reach * reach;
        Entity entity = null;
        Vector3d Vector3d = null;
        for (Entity e : world.getEntities(player, box)) {
            if (!pred.test(e))
                continue;
            AxisAlignedBB aabb = e.getBoundingBox().inflate(e.getPickRadius());
            Optional<Vector3d> optional = aabb.clip(pos, end);
            if (aabb.contains(pos)) {
                if (d0 >= 0.0D) {
                    entity = e;
                    Vector3d = optional.orElse(pos);
                    d0 = 0.0D;
                }
            } else if (optional.isPresent()) {
                Vector3d Vector3d1 = optional.get();
                double d1 = pos.distanceToSqr(Vector3d1);
                if (d1 < d0 || d0 == 0.0D) {
                    if (e.getRootVehicle() == player.getRootVehicle()) {
                        if (d0 == 0.0D) {
                            entity = e;
                            Vector3d = Vector3d1;
                        }
                    } else {
                        entity = e;
                        Vector3d = Vector3d1;
                        d0 = d1;
                    }
                }
            }
        }
        return entity == null ? null : new EntityRayTraceResult(entity, Vector3d);
    }

    private static EstiResult setAim(PlayerEntity pl, double velo, double reach, Entity e, double g, double k,
                                     int maxt) {
        EntityRayTraceResult ertr = rayTraceEntity(pl, reach, entity -> true);
        if (ertr != null && ertr.getType() == EntityRayTraceResult.Type.ENTITY) {
            if (ertr.getLocation().distanceTo(pl.position()) < velo)
                return EstiType.CLOSE;
            LogManager.getLogger().info("targeting entity: " + ertr.getEntity());
            Vector3d mot = ertr.getEntity().getDeltaMovement();
            Vector3d tar = ertr.getLocation();
            Vector3d pos = e.position();
            EstiResult er = new Estimator(g, k, pos, velo, maxt, tar, mot).getAnswer();
            LogManager.getLogger().info("aim status success: " + (er.getType() == EstiType.ZERO));
            if (er.getType() == EstiType.ZERO)
                return er;
        }
        BlockRayTraceResult brtr = rayTraceBlock(pl.level, pl, reach);
        if (brtr.getType() == BlockRayTraceResult.Type.BLOCK) {
            if (brtr.getLocation().distanceTo(pl.position()) < velo)
                return EstiType.CLOSE;
            LogManager.getLogger().info("targeting block: " + brtr.getLocation());
            Vector3d tar = brtr.getLocation();
            Vector3d pos = e.position();
            EstiResult er = new Estimator(g, k, pos, velo, maxt, tar, Vector3d.ZERO).getAnswer();
            LogManager.getLogger().info("aim status success: " + (er.getType() == EstiType.ZERO));
            if (er.getType() == EstiType.ZERO)
                return er;
        }
        return EstiType.FAIL;
    }

    private static void setDire(PlayerEntity pl, float velo, Entity ent) {
        float yaw = pl.yRot;
        float pitch = pl.xRot;
        float f = -MathHelper.sin(yaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
        float f1 = -MathHelper.sin(pitch * ((float) Math.PI / 180F));
        float f2 = MathHelper.cos(yaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
        Vector3d Vector3d = new Vector3d(f, f1, f2).normalize().scale(velo);
        ent.setDeltaMovement(Vector3d);
        float f3 = MathHelper.sqrt(Entity.getHorizontalDistanceSqr(Vector3d));
        ent.yRot = (float) (MathHelper.atan2(Vector3d.x, Vector3d.z) * (180F / (float) Math.PI));
        ent.xRot = (float) (MathHelper.atan2(Vector3d.y, f3) * (180F / (float) Math.PI));
        ent.xRotO = ent.xRot;
        ent.yRotO = ent.yRot;
    }

    public static class ShootConfig {

        public ItemStack bow;
        public ItemStack ammo;
        public World world;
        public PlayerEntity player;
        public float pullProgress;
        public boolean omitConsume;

        public float maxVelo = 3;
        public double power = 0.5;
        public int punch = 1;
        public int firetime = 100;
        public AbstractArrowEntity ppe = null;
        public ProjectileEntity pe = null;
        public Entity e = null;

        public double g, k;
        public int r, t;
        public float velo;

        public void setData(double g, double k, int r, int t) {
            this.g = g;
            this.k = k;
            this.r = r;
            this.t = t;
        }

    }

}
