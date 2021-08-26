package com.hikarishima.lightland.magic.arcane;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.magic.ThunderAxe;
import com.hikarishima.lightland.magic.arcane.magic.ThunderSword;
import com.hikarishima.lightland.magic.arcane.magic.WindBladeSword;

@SuppressWarnings("unused")
public class ArcaneRegistry {

    public static final int ARCANE_TIME = 600;

    public static final ThunderAxe MERAK_THUNDER = reg("thunder_axe", new ThunderAxe(10, 64f));
    public static final ThunderSword ALKAID_THUNDER = reg("thunder_sword", new ThunderSword(20, 32f));
    public static final WindBladeSword ALIOTH_WINDBLADE = reg("wind_blade", new WindBladeSword(5f, 1f, 32f));

    private static <T extends Arcane> T reg(String str, T a) {
        a.setRegistryName(LightLandMagic.MODID, str);
        return a;
    }

}
