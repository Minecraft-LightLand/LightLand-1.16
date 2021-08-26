package com.hikarishima.lightland.magic.registry.item.combat;

import com.hikarishima.lightland.event.combat.ISpecialWeapon;
import com.hikarishima.lightland.event.combat.MagicDamageEntry;
import com.hikarishima.lightland.event.combat.MagicDamageSource;
import com.hikarishima.lightland.magic.arcane.ArcaneRegistry;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneItemUseHelper;
import com.hikarishima.lightland.magic.arcane.internal.IArcaneItem;
import com.hikarishima.lightland.magic.registry.VanillaMagicRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightLandFakeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

public interface IArcaneWeapon extends ISpecialWeapon {

    @Nullable
    @Override
    default MagicDamageSource getSource(ItemStack stack, LivingHurtEvent event) {
        return toMagic(stack, event.getSource().getDirectEntity(), null, event.getSource(), event.getAmount(), ArcaneRegistry.ARCANE_TIME);
    }

    static MagicDamageSource toMagic(ItemStack stack, Entity entity, Entity owner, DamageSource s, float f, int time) {
        MagicDamageSource source = new MagicDamageSource(entity, owner);
        source.add(new MagicDamageEntry(s, f).setPost(e -> {
            if (stack.getItem() instanceof IArcaneItem) {
                ArcaneItemUseHelper.addArcaneMana(stack, (int) f);
            }
            LightLandFakeEntity.addEffect(e, new EffectInstance(VanillaMagicRegistry.ARCANE, time));
        }));
        return source;
    }

}
