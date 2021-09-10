package com.hikarishima.lightland.magic.arcane.magic;

import com.hikarishima.lightland.event.combat.MagicDamageEntry;
import com.hikarishima.lightland.event.combat.MagicDamageSource;
import com.hikarishima.lightland.magic.arcane.ArcaneRegistry;
import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class DamageAxe extends Arcane {

    private final float damage;

    public DamageAxe(int cost, float damage) {
        super(ArcaneType.DUBHE, cost);
        this.damage = damage;
    }

    @Override
    public boolean activate(PlayerEntity player, MagicHandler magic, ItemStack stack, LivingEntity target) {
        if (target == null)
            return false;
        World w = player.level;
        if (w.isClientSide())
            return true;
        MagicDamageSource source = new MagicDamageSource(player, player);
        source.add(new MagicDamageEntry(DamageSource.playerAttack(player).bypassArmor(), damage));
        source.add(new MagicDamageEntry(DamageSource.playerAttack(player).bypassMagic(), damage));
        source.add(new MagicDamageEntry(DamageSource.playerAttack(player).bypassArmor().bypassMagic(), damage)
                .setPost(ArcaneRegistry.postDamage(stack)));
        target.hurt(source, damage);
        return true;
    }

}
