package com.hikarishima.lightland.magic;

import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.lcy0x1.core.magic.HexHandler;
import com.lcy0x1.core.util.NBTObj;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Temporary object similar to ItemStack. It is created from the MagicBook Instance and can only be used
 * in a closed environment, which means no other source of MagicProduct can be assigned to the same variable.
 * <br><br>
 * It has 2 registered references: MagicProductType and Type-Specific Objects, such as Enchantment and Effects.
 * Each individual class of Type-Specific Object should have a corresponding subclass of MagicProduct.
 * <br><br>
 * List of known source of MagicProduct: <br>
 * 1. MagicBookContainer
 * 2. MagicBookManager (temporary, dispose item after function calls)
 * 3. IMagicProduct (null player and null tag, should not be casted to MagicProduct)
 * <hr>
 * Tags: <br>
 * 1. _base - It stores general information about a MagicProduct Instance. <br>
 * 2. hex - It stores the attempted or completed graph of this magic. <br>
 */
public class MagicProduct<I extends IForgeRegistryEntry<I>, P extends MagicProduct<I, P>> extends IMagicProduct<I,P>{

    public final NBTObj tag;
    public final PlayerEntity player;
    private final IMagicRecipe<?> recipe;
    private HexHandler best;

    public MagicProduct(MagicProductType<I, P> type, PlayerEntity player, NBTObj tag, ResourceLocation rl) {
        super(type, rl);
        this.tag = tag;
        this.player = player;
        this.recipe = IMagicRecipe.getRecipe(player.level, this);
        if (tag != null) {
            if (!tag.tag.contains("_base")) {
                // TODO
            }
            if (tag.tag.contains("hex"))
                best = new HexHandler(tag.getSub("hex"));
        }
    }

    protected final NBTObj getBase(){
        return tag.getSub("_base");
    }

    public final boolean unlocked() {
        return getBase().tag.getInt("cost") > -2;
    }

    public void updateBestSolution(HexHandler hex, int cost) {
        int prev = getBase().tag.getInt("cost");
        if (prev >= 0 && (cost < 0 || cost > prev))
            return;
        best = hex;
        tag.tag.remove("hex");
        hex.write(tag.getSub("hex"));
        getBase().tag.putInt("cost", cost);
    }

    public final boolean usable() {
        return getBase().tag.getInt("cost") > -1;
    }

}
