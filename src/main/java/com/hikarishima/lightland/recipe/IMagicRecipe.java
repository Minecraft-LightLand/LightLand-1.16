package com.hikarishima.lightland.recipe;

import com.hikarishima.lightland.magic.IMagicProduct;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.registry.RegistryBase;
import com.lcy0x1.base.BaseRecipe;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

@SerialClass
public class IMagicRecipe<R extends IMagicRecipe<R>> extends BaseRecipe<R, IMagicRecipe<?>, IMagicRecipe.Inv> {

    public interface Inv extends RecInv<IMagicRecipe<?>> {

    }

    @SuppressWarnings("unchecked")
    public static IMagicRecipe<?> getRecipe(World w, IMagicProduct<?, ?> p) {
        List<IMagicRecipe<?>> list = new ArrayList<>();
        RegistryBase.process(RecipeRegistry.class, BaseRecipe.RecType.class, (t) -> {
            if (!IMagicRecipe.class.isAssignableFrom(t.cls))
                return;
            for (IMagicRecipe<?> r : w.getRecipeManager().getAllRecipesFor((IRecipeType<IMagicRecipe<?>>) t.type)) {
                if (r.product_type == p.type && r.product_id.equals(p.rl))
                    list.add(r);
            }
        });
        if (list.size() == 0)
            return null;
        else if (list.size() > 1) LogManager.getLogger().error("repeated recipe for " + p + " : " + list);
        return list.get(0);

    }

    @SuppressWarnings("unchecked")
    public static List<IMagicRecipe<?>> getDependent(World w, IMagicRecipe<?> p) {
        List<IMagicRecipe<?>> list = new ArrayList<>();
        RegistryBase.process(RecipeRegistry.class, BaseRecipe.RecType.class, (t) -> {
            if (!IMagicRecipe.class.isAssignableFrom(t.cls))
                return;
            for (IMagicRecipe<?> r : w.getRecipeManager().getAllRecipesFor((IRecipeType<IMagicRecipe<?>>) t.type)) {
                if (r.predecessor.length == 0 && p == null)
                    list.add(r);
                else if (p != null) {
                    boolean find = false;
                    for (ResourceLocation rl : r.predecessor)
                        if (rl.equals(p.id))
                            list.add(r);
                }
            }
        });
        return list;
    }

    @SerialClass
    public static class ElementalMastery {

        @SerialClass.SerialField
        public MagicElement element;

        @SerialClass.SerialField
        public int level;

    }

    @SerialClass.SerialField
    public ResourceLocation[] predecessor;

    @SerialClass.SerialField
    public ElementalMastery[] elemental_mastery;

    @SerialClass.SerialField
    public MagicRegistry.MPTRaw product_type;

    @SerialClass.SerialField
    public ResourceLocation product_id;

    public IMagicRecipe(ResourceLocation id, RecType<R, IMagicRecipe<?>, Inv> fac) {
        super(id, fac);
    }

    @Override
    public final boolean matches(Inv inv, World world) {
        return false;
    }

    @Override
    public final ItemStack assemble(Inv inv) {
        return null;
    }

    @Override
    public final boolean canCraftInDimensions(int r, int c) {
        return false;
    }

    @Override
    public final ItemStack getResultItem() {
        return null;
    }

    public final IMagicProduct<?, ?> getProduct() {
        return IMagicProduct.getInstance(product_type, product_id);
    }

}
