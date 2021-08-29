package com.hikarishima.lightland.magic.registry.item.combat;

import com.hikarishima.lightland.event.combat.ISpecialWeapon;
import com.hikarishima.lightland.event.combat.MagicDamageEntry;
import com.hikarishima.lightland.event.combat.MagicDamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.function.Consumer;

public class MagicAxe extends AxeItem implements ISpecialWeapon {

    public final float prob, pen;

    public MagicAxe(MagicItemTier tier, int atk, float speed, Properties props) {
        super(tier, atk, speed, props.stacksTo(1));
        prob = tier.getProb() * 2;
        pen = tier.getPenetrate();
    }

    @Override
    public MagicDamageSource getSource(ItemStack stack, LivingHurtEvent event) {
        LivingEntity le = event.getEntityLiving();
        for (ItemStack armor : le.getArmorSlots()) {
            if (MagicArmor.disenchant(le.level, armor, prob))
                break;
        }
        MagicDamageSource source = new MagicDamageSource(event.getSource().getDirectEntity());
        if (pen < 1) source.add(new MagicDamageEntry(event.getSource(), event.getAmount() * (1 - pen)));
        if (pen > 0) source.add(new MagicDamageEntry(event.getSource(), event.getAmount() * pen).setBypassMagic());
        return source;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return getMaxDamage(stack) > 0 ? amount : 0;
    }

}
