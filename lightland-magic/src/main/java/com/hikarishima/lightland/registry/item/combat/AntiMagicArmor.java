package com.hikarishima.lightland.registry.item.combat;

import com.hikarishima.lightland.event.combat.ISpecialArmor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.Map;

public class AntiMagicArmor extends ArmorItem implements ISpecialArmor {

    private final float resist, prob;

    public AntiMagicArmor(AntiMagicArmorMaterial mat, EquipmentSlotType slot, Properties props) {
        super(mat, slot, props);
        this.resist = mat.getResist();
        this.prob = mat.getProb();
    }

    public static boolean disenchant(World w, ItemStack stack, float prob) {
        if (!stack.isEmpty() && stack.isEnchanted() && w.getRandom().nextDouble() < prob) {
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
            if (map.isEmpty())
                return false;
            int ind = w.getRandom().nextInt(map.size());
            Enchantment enc = map.keySet().toArray(new Enchantment[0])[ind];
            int lv = map.get(enc) - 1;
            if (lv <= 0) map.remove(enc);
            else map.put(enc, lv);
            EnchantmentHelper.setEnchantments(map, stack);
            return true;
        }
        return false;
    }

    @Override
    public float modifier(LivingEntity owner, DamageSource source, float original) {
        if (source.getDirectEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) source.getDirectEntity();
            ItemStack stack = attacker.getMainHandItem();
            disenchant(owner.level, stack, prob);
        }
        return source.isMagic() ? -resist * original - 1e-3f : 0;
    }

}
