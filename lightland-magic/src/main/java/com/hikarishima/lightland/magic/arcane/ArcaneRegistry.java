package com.hikarishima.lightland.magic.arcane;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.magic.ThunderAxe;
import com.hikarishima.lightland.magic.arcane.magic.ThunderSword;

public class ArcaneRegistry {

    public static final ThunderAxe MERAK_THUNDER = reg("thunder_axe", new ThunderAxe(1f));
    public static final ThunderSword ALKAID_THUNDER = reg("thunder_sword", new ThunderSword(32f));

    private static <T extends Arcane> T reg(String str, T a) {
        a.setRegistryName(LightLand.MODID, str);
        return a;
    }

}
