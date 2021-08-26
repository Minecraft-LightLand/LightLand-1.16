package com.hikarishima.lightland.magic.spell;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.spell.magic.DirtWallSpell;
import com.hikarishima.lightland.magic.spell.magic.EvokerFangSpell;
import com.hikarishima.lightland.magic.spell.magic.FireArrowSpell;
import com.hikarishima.lightland.magic.spell.magic.WindBladeSpell;
import net.minecraftforge.registries.ForgeRegistryEntry;

@SuppressWarnings("unused")
public class SpellRegistry {

    public static final DirtWallSpell EARTH_WALL = reg("dirt_wall", new DirtWallSpell());
    public static final DirtWallSpell EARTH_SLAB = reg("stone_slab", new DirtWallSpell());
    public static final DirtWallSpell EARTH_BLOCK = reg("stone_block", new DirtWallSpell());
    public static final EvokerFangSpell FANG_ROUND = reg("fang_round", new EvokerFangSpell());
    public static final WindBladeSpell BLADE_SIDE = reg("blade_side", new WindBladeSpell());
    public static final WindBladeSpell BLADE_FRONT = reg("blade_front", new WindBladeSpell());
    public static final FireArrowSpell FIRE_RAIN = reg("fire_rain", new FireArrowSpell());

    private static <V extends T, T extends ForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(LightLandMagic.MODID, name);
        return v;
    }

}
