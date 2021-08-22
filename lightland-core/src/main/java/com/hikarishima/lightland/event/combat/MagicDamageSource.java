package com.hikarishima.lightland.event.combat;

import com.lcy0x1.core.util.ExceptionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MagicDamageSource extends EntityDamageSource {

    private final List<MagicDamageEntry> list = new ArrayList<>();

    private final Entity owner;

    public MagicDamageSource(Entity e) {
        this( e, null);
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
        return owner == null ? entity : owner;
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
        ExceptionHandler.run(() -> {
            Method m = ExceptionHandler.getMethod(e.getClass(), "hurtArmor", DamageSource.class, float.class);
            m.invoke(e, ent.source, dmg * ent.armorDamageFactor);
        });
        return CombatRules.getDamageAfterAbsorb(dmg, (float) e.getArmorValue(), (float) e.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
    }

    public static float getDamageAfterMagicAbsorb(LivingEntity e, DamageSource ds, float f) {
        Float ans = ExceptionHandler.get(() -> {
            Method m = ExceptionHandler.getMethod(e.getClass(), "getDamageAfterArmorAbsorb", DamageSource.class, float.class);
            return (Float) m.invoke(e, ds, f);
        });
        return ans == null ? f : ans;
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
            ent.execute(e);
            ans += dmg;
        }
        return ans;
    }

}
