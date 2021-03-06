package com.hikarishima.lightland.event.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LightLandFakeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MagicDamageSource extends EntityDamageSource {

    private final List<MagicDamageEntry> list = new ArrayList<>();

    private final Entity owner;

    public MagicDamageSource(Entity e) {
        this(e, e);
    }

    public MagicDamageSource(Entity e, Entity owner) {
        super("lightland", e);
        this.owner = owner;
    }

    @Nullable
    public Entity getDirectEntity() {
        return entity;
    }

    @Nullable
    public Entity getEntity() {
        return owner;
    }

    public static float getDamageAfterArmorAbsorb(LivingEntity e, MagicDamageEntry ent, float f) {
        float mod = 0;
        for (ItemStack stack : e.getArmorSlots()) {
            if (stack.getItem() instanceof ISpecialArmor) {
                ISpecialArmor arm = (ISpecialArmor) stack.getItem();
                mod += arm.modifier(e, ent.source, f);
            }
        }
        float dmg = f + mod;
        if (dmg <= 0) {
            return 0;
        }
        if (ent.armorDamageFactor > 1)
            LightLandFakeEntity.hurtArmor(e, ent.source, dmg * (ent.armorDamageFactor - 1));
        return LightLandFakeEntity.getDamageAfterArmorAbsorb(e, ent.source, dmg);
    }

    public static float getDamageAfterMagicAbsorb(LivingEntity e, DamageSource ds, float f) {
        return LightLandFakeEntity.getDamageAfterMagicAbsorb(e, ds, f);
    }

    public boolean isBypassArmor() {
        return true;
    }

    public boolean isBypassMagic() {
        return true;
    }

    public void add(MagicDamageEntry ent) {
        list.add(ent);
    }

    public float getDamage(LivingEntity e) {
        float ans = 0;
        for (MagicDamageEntry ent : list) {
            float dmg = ent.damage;
            if (!ent.bypassArmor)
                dmg = getDamageAfterArmorAbsorb(e, ent, dmg);
            if (!ent.bypassMagic)
                dmg = getDamageAfterMagicAbsorb(e, ent.source, dmg);
            ans += dmg;
        }
        for (MagicDamageEntry ent : list) {
            ent.execute(e, ans);
        }
        return ans;
    }

}
