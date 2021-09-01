package com.hikarishima.lightland.magic.arcane.magic;

import com.hikarishima.lightland.magic.arcane.ArcaneRegistry;
import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.registry.VanillaMagicRegistry;
import net.minecraft.entity.LightLandFakeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class MarkerSword extends Arcane {

    public final float radius;

    public MarkerSword(int cost, float radius) {
        super(ArcaneType.ALKAID, cost);
        this.radius = radius;
    }

    @Override
    public boolean activate(PlayerEntity player, MagicHandler magic, ItemStack stack, LivingEntity target) {
        World w = player.level;
        if (!w.isClientSide()) {
            w.getEntities(player, new AxisAlignedBB(player.blockPosition()).inflate(radius), e -> {
                if (!(e instanceof LivingEntity) || !(e instanceof IMob))
                    return false;
                return e != player && e != target && !e.isAlliedTo(e);
            }).forEach(e -> LightLandFakeEntity.addEffect((LivingEntity) e, new EffectInstance(VanillaMagicRegistry.EFF_ARCANE.get(), ArcaneRegistry.ARCANE_TIME)));
        }
        return true;
    }
}
