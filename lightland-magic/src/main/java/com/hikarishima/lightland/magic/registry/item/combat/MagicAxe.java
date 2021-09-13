package com.hikarishima.lightland.magic.registry.item.combat;

import com.hikarishima.lightland.event.combat.ISpecialWeapon;
import com.hikarishima.lightland.event.combat.MagicDamageEntry;
import com.hikarishima.lightland.event.combat.MagicDamageSource;
import com.hikarishima.lightland.magic.Translator;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
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
        return getMaxDamage(stack) == 99999 ? 0 : amount;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        if (pen > 0) {
            list.add(Translator.get("tooltip.magic_penetrate", (int) (pen * 100) + "%"));
        }
        if (prob > 0) {
            list.add(Translator.get("tooltip.disenchant", (int) (prob * 100) + "%"));
        }
        super.appendHoverText(stack, world, list, flag);
    }

    @Override
    public void inventoryTick(ItemStack stack, World w, Entity e, int slot, boolean selected) {
        if (getMaxDamage(stack) % 99999 == 0 && stack.getDamageValue() > 0) {
            stack.setDamageValue(0);
        }
    }

}
