package com.hikarishima.lightland.item.arcane.internal;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.lcy0x1.base.NamedEntry;

/**
 * 天枢，天璇，天玑，天权，玉衡，开阳，摇光
 * DUBHE, MERAK, PHECDA, MEGREZ, ALIOTH, MIZAR, ALKAID
 */
public class ArcaneType extends NamedEntry<ArcaneType> {

    public static final ArcaneType DUBHE = reg("dubhe", new ArcaneType(Weapon.AXE, Hit.LIGHT, Mana.ACTIVE));
    public static final ArcaneType MERAK = reg("merak", new ArcaneType(Weapon.AXE, Hit.CRITICAL, Mana.ACTIVE));
    public static final ArcaneType PHECDA = reg("phecda", new ArcaneType(Weapon.AXE, Hit.CRITICAL, Mana.PASSIVE));
    public static final ArcaneType MEGREZ = reg("megrez", new ArcaneType(Weapon.AXE, Hit.LIGHT, Mana.PASSIVE));
    public static final ArcaneType ALIOTH = reg("alioth", new ArcaneType(Weapon.SWORD, Hit.LIGHT, Mana.PASSIVE));
    public static final ArcaneType MIZAR = reg("mizar", new ArcaneType(Weapon.SWORD, Hit.CRITICAL, Mana.PASSIVE));
    public static final ArcaneType ALKAID = reg("alkaid", new ArcaneType(Weapon.SWORD, Hit.NONE, Mana.ACTIVE));
    public final Weapon weapon;
    public final Hit hit;
    public final Mana mana;

    public ArcaneType(Weapon weapon, Hit hit, Mana mana) {
        super(() -> MagicRegistry.ARCANE_TYPE);
        this.weapon = weapon;
        this.hit = hit;
        this.mana = mana;
    }

    private static ArcaneType reg(String str, ArcaneType type) {
        type.setRegistryName(LightLand.MODID, str);
        return type;
    }

    public enum Weapon {
        SWORD, AXE
    }

    public enum Hit {
        LIGHT, CRITICAL, NONE
    }

    public enum Mana {
        PASSIVE, ACTIVE
    }
}
