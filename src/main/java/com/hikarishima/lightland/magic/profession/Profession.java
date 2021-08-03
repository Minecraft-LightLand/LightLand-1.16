package com.hikarishima.lightland.magic.profession;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.AbilityPoints;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.lcy0x1.base.NamedEntry;
import net.minecraft.util.ResourceLocation;

public abstract class Profession extends NamedEntry<Profession> {

    public static final String ERR_PREFIX = "screen.ability.ability.error.";

    public Profession() {
        super(() -> MagicRegistry.PROFESSION);
    }

    public abstract void init(MagicHandler handler);

    public abstract void levelUp(MagicHandler handler);

    public ResourceLocation getIcon() {
        ResourceLocation rl = getRegistryName();
        return new ResourceLocation(rl.getNamespace(), "textures/profession/" + rl.getPath() + ".png");
    }

    /**
     * return null if pass
     */
    public String allowLevel(AbilityPoints.LevelType type, MagicHandler handler) {
        if (type == AbilityPoints.LevelType.SPELL) {
            int lv = handler.magicAbility.spell_level;

        }
        //TODO
        return null;
    }

}
