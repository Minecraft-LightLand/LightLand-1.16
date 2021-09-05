package com.hikarishima.lightland.event.combat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MagicDamageEntry {

    public DamageSource source;
    public float armorDamageFactor = 1;
    public float damage;
    public boolean bypassArmor;
    public boolean bypassMagic;
    public BiConsumer<LivingEntity, Float> post;

    public MagicDamageEntry(DamageSource source, float amount) {
        this.source = source;
        this.damage = amount;
        bypassArmor = source.isBypassArmor();
        bypassMagic = source.isBypassMagic();
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

    public MagicDamageEntry setPost(BiConsumer<LivingEntity, Float> cons) {
        post = cons;
        return this;
    }

    public void execute(LivingEntity target, float damage) {
        if (post != null)
            post.accept(target, damage);
    }

}
