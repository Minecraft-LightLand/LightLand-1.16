package com.hikarishima.lightland.magic.arcane.magic;

import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.registry.VanillaMagicRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ThunderAxe extends Arcane {

    private final float radius;

    public ThunderAxe(int cost, float radius) {
        super(ArcaneType.MERAK, cost);
        this.radius = radius;
    }

    @Override
    public boolean activate(PlayerEntity player, MagicHandler magic, ItemStack stack, LivingEntity target) {
        if (target == null)
            return false;
        BlockPos pos = target.blockPosition();
        World w = player.level;
        strike(w, player, target);
        if (!w.isClientSide()) {
            w.getEntities(player, new AxisAlignedBB(player.blockPosition()).inflate(radius), e -> {
                if (!(e instanceof LivingEntity))
                    return false;
                if (e == player || e == target || e.isAlliedTo(e))
                    return false;
                return ((LivingEntity) e).hasEffect(VanillaMagicRegistry.EFF_ARCANE);
            }).forEach(e -> strike(w, player, (LivingEntity) e));
        }
        return w.canSeeSky(pos);
    }

    private void strike(World w, PlayerEntity player, LivingEntity target) {
        BlockPos pos = target.blockPosition();
        if (!w.isClientSide() && w.canSeeSky(pos)) {
            LightningBoltEntity e = EntityType.LIGHTNING_BOLT.create(w);
            e.moveTo(Vector3d.atBottomCenterOf(pos));
            e.setCause(player instanceof ServerPlayerEntity ? (ServerPlayerEntity) player : null);
            w.addFreshEntity(e);
            e.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 5f, 1.0F);
        }
    }

}
