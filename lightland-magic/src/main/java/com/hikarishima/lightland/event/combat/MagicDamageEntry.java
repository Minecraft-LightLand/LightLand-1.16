package com.hikarishima.lightland.event.combat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public class MagicDamageEntry {

    public DamageSource source;
    public float armorDamageFactor = 1;
    public float damage;
    public boolean bypassArmor = false;
    public boolean bypassMagic = false;

    public MagicDamageEntry(DamageSource source, float amount) {
        this.source = source;
        this.damage = amount;
    }

    public MagicDamageEntry setDamageArmor(float factor) {
        this.armorDamageFactor *= factor;
        return this;
    }

    public MagicDamageEntry setBypassArmor() {
        this.armorDamageFactor = 0;
        this.bypassArmor = true;
        return this;
    }

    public MagicDamageEntry setBypassMagic() {
        this.bypassMagic = true;
        return this;
    }

    public void execute(LivingEntity target) {

    }

}
