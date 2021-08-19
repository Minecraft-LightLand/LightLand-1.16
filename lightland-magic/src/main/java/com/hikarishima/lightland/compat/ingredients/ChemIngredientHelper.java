package com.hikarishima.lightland.compat.ingredients;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.config.StringSubstitution;
import com.hikarishima.lightland.magic.chem.ChemObj;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.ingredients.IIngredientHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ChemIngredientHelper implements IIngredientHelper<ChemIngredient> {

    @Nullable
    @Override
    public ChemIngredient getMatch(Iterable<ChemIngredient> iterable, ChemIngredient abChemObj) {
        for (ChemIngredient obj : iterable)
            if (obj.obj == abChemObj.obj)
                return obj;
        return null;
    }

    @Override
    public String getDisplayName(ChemIngredient abChemObj) {
        return abChemObj.obj instanceof ChemObj ? StringSubstitution.toString(((ChemObj<?, ?>) abChemObj.obj).getDesc()) : "???";
    }

    @Override
    public String getUniqueId(ChemIngredient abChemObj) {
        return abChemObj.obj.id;
    }

    @Override
    public String getModId(ChemIngredient abChemObj) {
        return LightLand.MODID;
    }

    @Override
    public String getResourceId(ChemIngredient abChemObj) {
        return abChemObj.obj.id;
    }

    @Override
    public ChemIngredient copyIngredient(ChemIngredient abChemObj) {
        return new ChemIngredient(abChemObj.obj);
    }

    @Override
    public String getErrorInfo(@Nullable ChemIngredient abChemObj) {
        return abChemObj == null ? "alchemistry object" : abChemObj.obj.id;
    }

}
