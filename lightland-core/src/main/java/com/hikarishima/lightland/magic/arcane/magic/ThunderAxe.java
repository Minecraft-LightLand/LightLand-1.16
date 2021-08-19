package com.hikarishima.lightland.magic.arcane.magic;

import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ThunderAxe extends Arcane {

    public ThunderAxe(float prob) {
        super(ArcaneType.MERAK, 3);
    }

    @Override
    public boolean activate(PlayerEntity player, MagicHandler magic, ItemStack stack, LivingEntity target) {
        if (target == null)
            return false;
        BlockPos pos = target.blockPosition();
        World w = player.level;
        if (!w.isClientSide() && w.canSeeSky(pos)) {
            LightningBoltEntity e = EntityType.LIGHTNING_BOLT.create(w);
            e.moveTo(Vector3d.atBottomCenterOf(pos));
            e.setCause(player instanceof ServerPlayerEntity ? (ServerPlayerEntity) player : null);
            w.addFreshEntity(e);
            e.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 5f, 1.0F);
        }
        return w.canSeeSky(pos);
    }
}
