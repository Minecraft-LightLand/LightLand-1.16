package com.hikarishima.lightland.magic.compat.ingredients;

import com.hikarishima.lightland.magic.MagicElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class ElementIngredient extends Countable {

    public final MagicElement elem;

    public ElementIngredient(MagicElement elem, int count) {
        this.elem = elem;
        this.count = count;
    }

    public ElementIngredient(MagicElement elem) {
        this(elem, 1);
    }

    public ElementIngredient(ElementIngredient elem) {
        this(elem.elem, elem.count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementIngredient that = (ElementIngredient) o;
        return Objects.equals(elem, that.elem);
    }

    @Override
    public int hashCode() {
        return elem.hashCode();
    }
}
