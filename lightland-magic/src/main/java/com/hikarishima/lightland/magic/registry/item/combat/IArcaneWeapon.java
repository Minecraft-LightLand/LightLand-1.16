package com.hikarishima.lightland.magic.registry.item.combat;

import com.hikarishima.lightland.event.combat.ISpecialWeapon;
import com.hikarishima.lightland.event.combat.MagicDamageEntry;
import com.hikarishima.lightland.event.combat.MagicDamageSource;
import com.hikarishima.lightland.magic.registry.VanillaMagicRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

public interface IArcaneWeapon extends ISpecialWeapon {

    @Nullable
    @Override
    default MagicDamageSource getSource(ItemStack stack, LivingHurtEvent event) {
        return toMagic(event.getSource().getDirectEntity(), event.getSource(), event.getAmount(), 200);
    }

    static MagicDamageSource toMagic(Entity entity, DamageSource s, float f, int time) {
        MagicDamageSource source = new MagicDamageSource(entity);
        source.add(new MagicDamageEntry(s, f).setPost(e -> e.addEffect(new EffectInstance(VanillaMagicRegistry.ARCANE, time))));
        return source;
    }

}
