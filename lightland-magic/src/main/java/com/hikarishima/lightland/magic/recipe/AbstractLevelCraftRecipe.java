package com.hikarishima.lightland.magic.recipe;

import com.hikarishima.lightland.magic.registry.block.RitualCore;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

@SerialClass
public class AbstractLevelCraftRecipe<R extends AbstractLevelCraftRecipe<R>> extends AbstractMagicCraftRecipe<R> {

    @SerialClass.SerialField
    public ResourceLocation magic_recipe;

    @SerialClass.SerialField
    public int[] levels;

    public AbstractLevelCraftRecipe(ResourceLocation id, RecType<R, AbstractMagicCraftRecipe<?>, RitualCore.Inv> fac) {
        super(id, fac);
    }

    @Nullable
    public ResourceLocation getMagic() {
        return magic_recipe;
    }

    public int getLevel(int cost) {
        for (int i = 0; i < levels.length; i++) {
            if (cost > levels[i]) {
                return i;
            }
        }
        return levels.length;
    }

    public int getNextLevel(int cost) {
        for (int level : levels) {
            if (cost > level) {
                return level;
            }
        }
        return 0;
    }

}
