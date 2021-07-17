package com.hikarishima.lightland.item.arcane;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.item.arcane.internal.Arcane;
import com.hikarishima.lightland.item.arcane.magic.ThunderAxe;

public class ArcaneRegistry {

    public static final ThunderAxe MERAK_THUNDER = new ThunderAxe(1f);

    private static <T extends Arcane> T reg(String str, T a) {
        a.setRegistryName(LightLand.MODID, str);
        return a;
    }

}
