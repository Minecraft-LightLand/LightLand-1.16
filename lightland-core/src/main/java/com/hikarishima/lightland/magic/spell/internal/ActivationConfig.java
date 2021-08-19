package com.hikarishima.lightland.magic.spell.internal;

import com.lcy0x1.core.math.AutoAim;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ActivationConfig {

    public World world;
    public PlayerEntity player;
    public Vector3d pos;
    public Vector3d dire;

    public ActivationConfig(World world, PlayerEntity player, double reach) {
        this.world = world;
        this.player = player;

        float f = player.xRot;
        float f1 = player.yRot;
        dire = AutoAim.getRayTerm(Vector3d.ZERO, f, f1, 1);

        EntityRayTraceResult ertr = AutoAim.rayTraceEntity(player, reach, (e) -> e instanceof LivingEntity && e != player);
        if (ertr != null) {
            pos = ertr.getLocation();
            return;
        }
        BlockRayTraceResult brtr = AutoAim.rayTraceBlock(world, player, reach);
        if (brtr.getType() == RayTraceResult.Type.BLOCK) {
            pos = brtr.getLocation();
            return;
        }
        Vector3d vector3d = new Vector3d(player.getX(), player.getEyeY(), player.getZ());
        pos = AutoAim.getRayTerm(vector3d, f, f1, reach);
    }


}
