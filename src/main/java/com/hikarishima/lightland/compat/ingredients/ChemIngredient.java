package com.hikarishima.lightland.compat.ingredients;

import com.hikarishima.lightland.compat.LightLandJeiPlugin;
import com.hikarishima.lightland.magic.chem.ChemElement;
import com.hikarishima.lightland.magic.chem.ChemItem;
import com.lcy0x1.core.chem.AbChemObj;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChemIngredient extends Countable {

    @Nullable
    public static ItemStack asItem(AbChemObj obj) {
        if (obj instanceof ChemItem)
            return new ItemStack(((ChemItem) obj).get());
        return null;
    }

    @Nullable
    public static ElementIngredient asElem(AbChemObj obj) {
        if (obj instanceof ChemElement)
            return new ElementIngredient(((ChemElement) obj).get(), 1);
        return null;
    }

    @Nullable
    public static ChemIngredient asChem(AbChemObj obj) {
        if (obj instanceof ChemItem || obj instanceof ChemElement)
            return null;
        return new ChemIngredient(obj);
    }

    public static IIngredientType<?> getType(AbChemObj obj) {
        return obj instanceof ChemItem ? VanillaTypes.ITEM :
                obj instanceof ChemElement ? LightLandJeiPlugin.INSTANCE.ELEM_TYPE :
                        LightLandJeiPlugin.INSTANCE.CHEM_TYPE;
    }

    public final AbChemObj obj;

    ChemIngredient(AbChemObj obj) {
        this.obj = obj;
    }

    public int hashCode() {
        return obj.hashCode();
    }

}
