package com.hikarishima.lightland.magic.arcane.magic;

import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.registry.entity.misc.WindBladeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class WindBladeSword extends Arcane {

    public final float dmg, velocity, dist;

    public WindBladeSword(float dmg, float velocity, float dist) {
        super(ArcaneType.ALIOTH, 0);
        this.dmg = dmg;
        this.velocity = velocity;
        this.dist = dist;
    }

    @Override
    public boolean activate(PlayerEntity player, MagicHandler magic, ItemStack stack, LivingEntity target) {
        if (target != null)
            return false;
        float strength = player.getAttackStrengthScale(0.5f);
        if (strength < 0.9f)
            return false;
        player.resetAttackStrengthTicker();
        World w = player.level;
        if (!w.isClientSide()) {
            WindBladeEntity e = new WindBladeEntity(w);
            e.setOwner(player);
            e.setPos(player.getX(), player.getEyeY() - 0.5f, player.getZ());
            e.shootFromRotation(player, player.xRot, player.yRot, 0, velocity, 1);
            e.setProperties(dmg, Math.round(dist / velocity), (float) (Math.random() * 360f), stack);
            w.addFreshEntity(e);
        }
        return true;
    }
}
