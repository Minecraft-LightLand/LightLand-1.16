package com.hikarishima.lightland.magic.arcane.internal;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import com.hikarishima.lightland.magic.registry.item.magic.ArcaneAxe;
import com.hikarishima.lightland.magic.registry.item.magic.ArcaneSword;
import com.lcy0x1.base.NamedEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    private final ItemStack stack;

    public ArcaneType(Weapon weapon, Hit hit, Mana mana) {
        super(() -> MagicRegistry.ARCANE_TYPE);
        this.weapon = weapon;
        this.hit = hit;
        this.mana = mana;
        stack = (weapon == Weapon.AXE ? MagicItemRegistry.ARCANE_AXE_GILDED : MagicItemRegistry.ARCANE_SWORD_GILDED).get().getDefaultInstance();
        if (mana == Mana.ACTIVE) {
            stack.getOrCreateTag().putBoolean("foil", true);
        }
    }

    private static ArcaneType reg(String str, ArcaneType type) {
        type.setRegistryName(LightLandMagic.MODID, str);
        return type;
    }

    @OnlyIn(Dist.CLIENT)
    public ItemStack getStack() {
        return stack;
    }

    public enum Weapon {
        SWORD(ArcaneSword.class), AXE(ArcaneAxe.class);

        private final Class<?> cls;

        Weapon(Class<?> cls) {
            this.cls = cls;
        }

        public boolean isValid(ItemStack stack) {
            return cls.isInstance(stack.getItem());
        }
    }

    public enum Hit {
        LIGHT, CRITICAL, NONE
    }

    public enum Mana {
        PASSIVE, ACTIVE
    }
}
