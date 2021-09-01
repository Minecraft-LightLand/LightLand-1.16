package com.hikarishima.lightland.magic.registry.enchantment;

import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PhysicsProtectionEnchantment extends ProtectionEnchantment {

    public PhysicsProtectionEnchantment() {
        super(Rarity.COMMON, Type.ALL, EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET);
    }

    public int getDamageProtection(int lv, DamageSource source) {
        if (source.isBypassInvul() || source.isMagic() || source.isFire() || source.isBypassArmor()) {
            return 0;
        }
        if (source == DamageSource.DRY_OUT || source == DamageSource.LIGHTNING_BOLT) {
            return 0;
        }
        return lv * 2;
    }

}
