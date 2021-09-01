package com.hikarishima.lightland.magic.registry.item.combat;

import com.hikarishima.lightland.event.combat.ISpecialArmor;
import com.hikarishima.lightland.magic.Translator;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class MagicArmor extends ArmorItem implements ISpecialArmor {

    private final float resist, prob;

    public MagicArmor(MagicArmorMaterial mat, EquipmentSlotType slot, Properties props) {
        super(mat, slot, props.stacksTo(1));
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

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return getMaxDamage(stack) > 0 ? amount : 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        if (resist > 0) {
            list.add(Translator.get("tooltip.magic_resist", (int) (resist * 100) + "%"));
        }
        if (prob > 0) {
            list.add(Translator.get("tooltip.disenchant",  (int) (prob * 100) + "%"));
        }
        super.appendHoverText(stack, world, list, flag);
    }

}
