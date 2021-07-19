package com.hikarishima.lightland.event.forge;

import com.hikarishima.lightland.event.combat.ISpecialArmor;
import com.hikarishima.lightland.event.combat.ISpecialWeapon;
import com.hikarishima.lightland.event.combat.MagicDamageSource;
import com.lcy0x1.core.util.ExceptionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class DamageEventHandler {

    @SubscribeEvent
    public void onLivingHurtEvent(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source instanceof MagicDamageSource) {
            MagicDamageSource magic = (MagicDamageSource) source;
            LivingEntity target = event.getEntityLiving();
            event.setAmount(magic.getDamage(target));
        } else {
            LivingEntity target = event.getEntityLiving();
            if (source.getDirectEntity() instanceof LivingEntity) {
                LivingEntity e = (LivingEntity) source.getDirectEntity();
                ItemStack stack = e.getMainHandItem();
                if (stack.getItem() instanceof ISpecialWeapon) {
                    ISpecialWeapon weapon = (ISpecialWeapon) stack.getItem();
                    MagicDamageSource magic = weapon.getSource(stack, event);
                    if (magic != null) {
                        ExceptionHandler.run(() -> {
                            Method m = target.getClass().getMethod("actuallyHurt", DamageSource.class, float.class);
                            m.invoke(target, magic, 0);
                        });
                        event.setAmount(-1);
                        event.setCanceled(true);
                        return;
                    }
                }
            }
            float original = event.getAmount();
            float mod = 0;
            for (ItemStack stack : target.getArmorSlots()) {
                if (stack.getItem() instanceof ISpecialArmor) {
                    ISpecialArmor arm = (ISpecialArmor) stack.getItem();
                    mod += arm.modifier(target, source, original);
                }
            }
            event.setAmount(original + mod);
        }
    }

}
