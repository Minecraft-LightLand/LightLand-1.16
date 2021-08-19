package com.hikarishima.lightland.magic.compat.ingredients;

import com.hikarishima.lightland.config.StringSubstitution;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.ingredients.IIngredientHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ElemIngredientHelper implements IIngredientHelper<ElementIngredient> {

    @Nullable
    @Override
    public ElementIngredient getMatch(Iterable<ElementIngredient> iterable, ElementIngredient magicElement) {
        for (ElementIngredient elem : iterable)
            if (elem.elem == magicElement.elem)
                return elem;
        return null;
    }

    @Override
    public String getDisplayName(ElementIngredient magicElement) {
        return StringSubstitution.toString(magicElement.elem.getDesc());
    }

    @Override
    public String getUniqueId(ElementIngredient magicElement) {
        return magicElement.elem.getID();
    }

    @Override
    public String getModId(ElementIngredient magicElement) {
        return magicElement.elem.getRegistryName().getNamespace();
    }

    @Override
    public String getResourceId(ElementIngredient magicElement) {
        return magicElement.elem.getIcon().toString();
    }

    @Override
    public ElementIngredient copyIngredient(ElementIngredient magicElement) {
        return new ElementIngredient(magicElement);
    }

    @Override
    public String getErrorInfo(@Nullable ElementIngredient magicElement) {
        return "magic element error";
    }
}
