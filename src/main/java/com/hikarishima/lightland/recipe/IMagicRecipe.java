package com.hikarishima.lightland.recipe;

import com.hikarishima.lightland.magic.products.IMagicProduct;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.lcy0x1.base.BaseRecipe;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

@SerialClass
public class IMagicRecipe<R extends IMagicRecipe<R>> extends BaseRecipe<R, IMagicRecipe<?>, IMagicRecipe.Inv> {

    @SerialClass.SerialField
    public ResourceLocation[] predecessor;
    @SerialClass.SerialField
    public ElementalMastery[] elemental_mastery;
    @SerialClass.SerialField
    public MagicRegistry.MPTRaw product_type;
    @SerialClass.SerialField
    public ResourceLocation product_id;
    @SerialClass.SerialField
    public BookScreen screen;
    private MagicElement[] elements;
    private boolean[][] maps;

    public IMagicRecipe(ResourceLocation id, RecType<R, IMagicRecipe<?>, Inv> fac) {
        super(id, fac);
    }

    public static List<IMagicRecipe<?>> getAll(World w) {
        return w.getRecipeManager().getAllRecipesFor(RecipeRegistry.RT_MAGIC);
    }

    public final IMagicProduct<?, ?> getProduct() {
        return IMagicProduct.getInstance(product_type, product_id);
    }

    protected final void register(MagicElement[] elements, boolean[][] maps) {
        this.elements = elements;
        this.maps = maps;
    }

    @Override
    public final boolean matches(Inv inv, World world) {
        return false;
    }

    @Override
    public final ItemStack assemble(Inv inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public final boolean canCraftInDimensions(int r, int c) {
        return false;
    }

    @Override
    public final ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    public interface Inv extends BaseRecipe.RecInv<IMagicRecipe<?>> {

    }

    @SerialClass
    public static class ElementalMastery {

        @SerialClass.SerialField
        public MagicElement element;

        @SerialClass.SerialField
        public int level;

    }

    @SerialClass
    public static class BookScreen {

        @SerialClass.SerialField
        public int screen_x, screen_y;

    }


}
