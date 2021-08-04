package com.hikarishima.lightland.magic.products;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.products.info.ProductState;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.lcy0x1.core.magic.HexHandler;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.NBTObj;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class MagicProduct<I extends IForgeRegistryEntry<I>, P extends MagicProduct<I, P>> extends IMagicProduct<I, P> {

    public static final int LOCKED = -2, UNLOCKED = -1;

    public final NBTObj tag;
    public final MagicHandler player;
    public final IMagicRecipe<?> recipe;

    public MagicProduct(MagicProductType<I, P> type, MagicHandler player, NBTObj tag, ResourceLocation rl, IMagicRecipe<?> r) {
        super(type, rl);
        this.tag = tag;
        this.player = player;
        this.recipe = r;
        if (tag != null) {
            if (!tag.tag.contains("_base")) {
                tag.getSub("_base").tag.putInt("cost", LOCKED);
            }
        }
    }

    public final NBTObj getBase() {
        return tag.getSub("_base");
    }

    public final boolean unlocked() {
        return getBase().tag.getInt("cost") > -2;
    }

    public final void setUnlock() {
        if (!unlocked())
            getBase().tag.putInt("cost", -1);
    }

    public void updateBestSolution(HexHandler hex, HexData data, int cost) {
        int prev = getBase().tag.getInt("cost");
        if (prev >= 0 && (cost < 0 || cost > prev))
            return;
        tag.tag.remove("hex");
        Automator.toTag(tag.getSub("misc").tag, data);
        hex.write(tag.getSub("hex"));
        getBase().tag.putInt("cost", cost);
    }

    public HexHandler getSolution() {
        return new HexHandler(tag.getSub("hex"));
    }

    public final boolean usable() {
        return getBase().tag.getInt("cost") > UNLOCKED;
    }

    public ProductState getState() {
        switch (getBase().tag.getInt("cost")) {
            case LOCKED:
                return ProductState.LOCKED;
            case UNLOCKED:
                return ProductState.UNLOCKED;
            default:
                return ProductState.CRAFTED;
        }
    }

    public boolean visible() {
        return true;
    }

    public HexData getMiscData() {
        if (tag.tag.contains("misc")) {
            return Automator.fromTag(tag.getSub("misc").tag, HexData.class);
        } else {
            return new HexData();
        }
    }

    public boolean matchList(List<MagicElement> elem) {
        return elem.equals(getMiscData().list);
    }

    @SerialClass
    public static class HexData {

        @SerialClass.SerialField
        public int[] order;

        @SerialClass.SerialField(generic = MagicElement.class)
        public ArrayList<MagicElement> list = new ArrayList<>();

    }

}
