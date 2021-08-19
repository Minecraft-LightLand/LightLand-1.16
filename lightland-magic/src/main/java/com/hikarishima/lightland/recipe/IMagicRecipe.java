package com.hikarishima.lightland.recipe;

import com.google.common.collect.Maps;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.products.IMagicProduct;
import com.hikarishima.lightland.magic.products.MagicProductType;
import com.hikarishima.lightland.magic.products.info.DisplayInfo;
import com.lcy0x1.base.BaseRecipe;
import com.lcy0x1.core.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SerialClass
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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
    public DisplayInfo screen;
    private MagicElement[] elements;
    private boolean[][] maps;

    public IMagicRecipe(ResourceLocation id, RecType<R, IMagicRecipe<?>, Inv> fac) {
        super(id, fac);
    }

    public static List<IMagicRecipe<?>> getAll(World w) {
        return w.getRecipeManager().getAllRecipesFor(RecipeRegistry.RT_MAGIC);
    }

    public static <T extends IForgeRegistryEntry<T>> Map<T, IMagicRecipe<?>> getMap(World w, MagicProductType<T, ?> type) {
        Map<T, IMagicRecipe<?>> ans = Maps.newLinkedHashMap();
        getAll(w).stream().filter(r -> r.product_type.getAsType() == type)
                .forEach(r -> Optional.of(type.getter.apply(r.product_id)).ifPresent((t) -> ans.put(t, r)));
        return ans;
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

    public final MagicElement[] getElements() {
        return elements;
    }

    public final boolean[][] getGraph() {
        return maps;
    }

    public interface Inv extends RecInv<IMagicRecipe<?>> {

    }

    @SerialClass
    public static class ElementalMastery {

        @SerialClass.SerialField
        public MagicElement element;

        @SerialClass.SerialField
        public int level;

    }

}
