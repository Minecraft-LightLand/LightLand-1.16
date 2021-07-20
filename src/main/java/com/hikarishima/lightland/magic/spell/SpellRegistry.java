package com.hikarishima.lightland.magic.spell;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.spell.magic.DirtWallSpell;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SpellRegistry {

    public static final DirtWallSpell EARTH_WALL = reg("dirt_wall", new DirtWallSpell());

    private static <V extends T, T extends ForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(LightLand.MODID, name);
        return v;
    }

}
