package com.hikarishima.lightland.magic.registry.item.combat;

import com.hikarishima.lightland.event.combat.ISpecialWeapon;
import com.hikarishima.lightland.event.combat.MagicDamageEntry;
import com.hikarishima.lightland.event.combat.MagicDamageSource;
import com.hikarishima.lightland.magic.arcane.ArcaneRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

public interface IArcaneWeapon extends ISpecialWeapon {

    @Nullable
    @Override
    default MagicDamageSource getSource(ItemStack stack, LivingHurtEvent event) {
        return toMagic(stack, event.getSource().getDirectEntity(), null, event.getSource(), event.getAmount());
    }

    static MagicDamageSource toMagic(ItemStack stack, Entity entity, Entity owner, DamageSource s, float f) {
        MagicDamageSource source = new MagicDamageSource(entity, owner);
        source.add(new MagicDamageEntry(s, f).setPost(ArcaneRegistry.postDamage(stack)));
        return source;
    }

}
