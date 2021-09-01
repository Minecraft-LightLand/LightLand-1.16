package com.hikarishima.lightland.magic.spell;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.spell.magic.*;
import net.minecraftforge.registries.ForgeRegistryEntry;

@SuppressWarnings("unused")
public class SpellRegistry {

    public static final DirtWallSpell EARTH_WALL = reg("dirt_wall", new DirtWallSpell());
    public static final DirtWallSpell EARTH_SLAB = reg("stone_slab", new DirtWallSpell());
    public static final DirtWallSpell EARTH_BLOCK = reg("stone_block", new DirtWallSpell());
    public static final EvokerFangSpell FANG_ROUND = reg("fang_round", new EvokerFangSpell());
    public static final WaterTrapSpell WATER_TRAP_0 = reg("water_trap_0", new WaterTrapSpell());
    public static final WaterTrapSpell WATER_TRAP_1 = reg("water_trap_1", new WaterTrapSpell());
    public static final WindBladeSpell BLADE_SIDE = reg("blade_side", new WindBladeSpell());
    public static final WindBladeSpell BLADE_FRONT = reg("blade_front", new WindBladeSpell());
    public static final FireArrowSpell FIRE_RAIN = reg("fire_rain", new FireArrowSpell());
    public static final FireArrowSpell EXPLOSION_RAIN = reg("explosion_rain", new FireArrowSpell());
    public static final FireArrowSpell FIRE_EXPLOSION = reg("fire_explosion", new FireArrowSpell());
    public static final PetrificationSpell PETR_0 = reg("petrification_0", new PetrificationSpell());
    public static final PetrificationSpell PETR_1 = reg("petrification_1", new PetrificationSpell());

    private static <V extends T, T extends ForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(LightLandMagic.MODID, name);
        return v;
    }

}
