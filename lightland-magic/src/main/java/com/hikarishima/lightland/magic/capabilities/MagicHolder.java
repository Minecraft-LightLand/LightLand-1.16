package com.hikarishima.lightland.magic.capabilities;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.products.MagicProductType;
import com.hikarishima.lightland.magic.recipe.IMagicRecipe;
import com.lcy0x1.core.util.NBTObj;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@SerialClass
public class MagicHolder {

    public static final int MAX_ELEMENTAL_MASTERY = 3;

    private final Map<MagicProductType<?, ?>, Map<ResourceLocation, MagicProduct<?, ?>>> product_cache = new HashMap<>();
    private final Map<ResourceLocation, IMagicRecipe<?>> recipe_cache = new HashMap<>();
    private final MagicHandler parent;

    @SerialClass.SerialField
    public CompoundNBT masteries = new CompoundNBT();
    @SerialClass.SerialField
    public CompoundNBT elements = new CompoundNBT();
    @SerialClass.SerialField
    public CompoundNBT products = new CompoundNBT();

    MagicHolder(MagicHandler parent) {
        this.parent = parent;
    }

    public int getElementalMastery(MagicElement elem) {
        return masteries.getInt(elem.getID());
    }

    public boolean addElementalMastery(MagicElement elem) {
        int current = getElementalMastery(elem);
        if (current >= MAX_ELEMENTAL_MASTERY)
            return false;
        masteries.putInt(elem.getID(), current + 1);
        checkUnlocks();
        return true;
    }

    void checkUnlocks() {
        List<IMagicRecipe<?>> list = IMagicRecipe.getAll(parent.world);
        for (IMagicRecipe<?> r : list) {
            recipe_cache.put(r.id, r);
        }
        for (IMagicRecipe<?> r : list) {
            if (isUnlocked(r))
                getProduct(r).setUnlock();
        }
    }

    private boolean isUnlocked(IMagicRecipe<?> r) {
        for (IMagicRecipe.ElementalMastery elem : r.elemental_mastery)
            if (getElementalMastery(elem.element) < elem.level)
                return false;
        for (ResourceLocation rl : r.predecessor) {
            MagicProduct<?, ?> prod = getProduct(recipe_cache.get(rl));
            if (prod != null && !prod.usable())
                return false;
        }
        return true;
    }

    @Nullable
    public IMagicRecipe<?> getRecipe(ResourceLocation rl) {
        return recipe_cache.get(rl);
    }

    public Collection<IMagicRecipe<?>> listRecipe() {
        return recipe_cache.values();
    }

    public MagicProduct<?, ?> getProduct(IMagicRecipe<?> r) {
        if (r == null)
            return null;
        MagicProductType<?, ?> type = r.product_type.getAsType();
        Map<ResourceLocation, MagicProduct<?, ?>> submap;
        if (!product_cache.containsKey(type))
            product_cache.put(type, submap = new HashMap<>());
        else submap = product_cache.get(type);
        if (submap.containsKey(r.product_id))
            return submap.get(r.product_id);
        NBTObj nbt = new NBTObj(products).getSub(type.getID()).getSub(r.product_id.toString());
        MagicProduct<?, ?> ans = type.fac.get(parent, nbt, r.product_id, r);
        submap.put(r.product_id, ans);
        return ans;
    }

    public void addElement(MagicElement elem, Integer val) {
        elements.putInt(elem.getID(), elements.getInt(elem.getID()) + val);
    }

    public int getElement(MagicElement elem) {
        return elements.getInt(elem.getID());
    }

    public IMagicRecipe<?> getTree(List<MagicElement> elem) {
        if (elem.size() == 0) {
            return null;
        }
        MagicElement type = elem.get(0);
        MagicProductType<?, ?> res = MagicRegistry.PRODUCT_TYPE.getValues().stream()
                .filter(e -> e.getAsType().elem == type).map(MagicRegistry.MPTRaw::getAsType)
                .findFirst().orElseThrow(() -> new NoSuchElementException("no matching type"));
        if (!product_cache.containsKey(res))
            return null;
        List<MagicProduct<?, ?>> ans = product_cache.get(res).values().stream()
                .filter(e -> e.usable() && e.matchList(elem)).collect(Collectors.toList());
        if (ans.size() == 1)
            return ans.get(0).recipe;
        return null;
    }

}
