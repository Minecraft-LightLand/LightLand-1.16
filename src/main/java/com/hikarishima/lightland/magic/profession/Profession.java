package com.hikarishima.lightland.magic.profession;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.AbilityPoints;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.lcy0x1.base.NamedEntry;
import net.minecraft.util.ResourceLocation;

public abstract class Profession extends NamedEntry<Profession> {

    public static final String ERR_PREFIX = "screen.ability.ability.error.";
    public static final String ERR_MAX = ERR_PREFIX + "max_reached";
    public static final String ERR_PROF_MAX = ERR_PREFIX + "prof_max";

    public Profession() {
        super(() -> MagicRegistry.PROFESSION);
    }

    public abstract void init(MagicHandler handler);

    /**
     * only AbilityPoints is allowed to change
     */
    public abstract void levelUp(MagicHandler handler);

    public ResourceLocation getIcon() {
        ResourceLocation rl = getRegistryName();
        return new ResourceLocation(rl.getNamespace(), "textures/profession/" + rl.getPath() + ".png");
    }

    /**
     * return null if pass
     * return failure reason if failed
     */
    public String allowLevel(AbilityPoints.LevelType type, MagicHandler handler) {
        int level = handler.abilityPoints.level;
        if (type == AbilityPoints.LevelType.SPELL) {
            int lv = handler.magicAbility.spell_level;
            if (lv == 9)
                return ERR_MAX;
            if (level < 10) {
                if (this == MagicRegistry.PROF_SPELL)
                    return null;
                if (this == MagicRegistry.PROF_MAGIC || this == MagicRegistry.PROF_ARCANE)
                    return lv >= 3 ? ERR_PROF_MAX : null;
                if (lv >= 1)
                    return ERR_PROF_MAX;
            }
        } else if (type == AbilityPoints.LevelType.MANA) {
            int lv = handler.magicAbility.magic_level;
            if (lv >= 10)
                return ERR_MAX;
            if (level < 10) {
                if (this == MagicRegistry.PROF_MAGIC)
                    return null;
                if (this == MagicRegistry.PROF_SPELL || this == MagicRegistry.PROF_ARCANE)
                    return lv >= 3 + level / 3 ? ERR_PROF_MAX : null;
                if (lv >= 3)
                    return ERR_PROF_MAX;
            }
        } else {
            int lv = type.level.apply(handler);
            if (lv >= 10)
                return ERR_MAX;
            if (level < 10) {
                if (this == MagicRegistry.PROF_MAGIC || this == MagicRegistry.PROF_SPELL)
                    return lv >= 1 + level / 5 ? ERR_PROF_MAX : null;
                if (this == MagicRegistry.PROF_ARCANE)
                    return lv >= 3 + level / 3 ? ERR_PROF_MAX : null;
            }
        }
        return null;
    }

}
