package com.hikarishima.lightland.magic.compat.ingredients;

import com.hikarishima.lightland.magic.chem.ChemElement;
import com.hikarishima.lightland.magic.chem.ChemItem;
import com.hikarishima.lightland.magic.compat.LightLandJeiPlugin;
import com.lcy0x1.core.chem.AbChemObj;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
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

    @Nullable
    public static Object as(@Nullable AbChemObj obj) {
        if (obj == null)
            return null;
        if (obj instanceof ChemItem)
            return asItem(obj);
        else if (obj instanceof ChemElement)
            return asElem(obj);
        else return asChem(obj);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChemIngredient that = (ChemIngredient) o;
        return Objects.equals(obj, that.obj);
    }

    public int hashCode() {
        return obj.hashCode();
    }

}
