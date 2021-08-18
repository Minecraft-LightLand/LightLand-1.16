package com.hikarishima.lightland.compat.screen;

import com.hikarishima.lightland.compat.ingredients.ChemIngredient;
import com.hikarishima.lightland.compat.ingredients.ElementIngredient;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.gui.container.AbstractScreen;
import com.hikarishima.lightland.magic.gui.container.ExtraInfo;
import com.lcy0x1.core.chem.AbChemObj;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class ExtraInfoScreen implements IGuiContainerHandler<AbstractScreen<?>> {

    public static final List<Wrapper> LIST = new ArrayList<>();

    public static void init() {
        LIST.add((a, b) -> (a instanceof MagicElement) && (b instanceof Integer) ? new ElementIngredient((MagicElement) a, (Integer) b) : null);
        LIST.add((a, b) -> (a instanceof AbChemObj) && (b instanceof Double) ? ChemIngredient.as((AbChemObj) a) : null);
    }

    @Nullable
    @Override
    public Object getIngredientUnderMouse(AbstractScreen<?> screen, double mx, double my) {
        ExtraInfo<?> extra = (ExtraInfo<?>) screen;
        List<Object> potential = new ArrayList<>();
        extra.getInfoMouse(mx - screen.getGuiLeft(), my - screen.getGuiTop(), (x, y, w, h, e) -> unwrap(potential, e));
        Optional<Object> ans = potential.stream().filter(Objects::nonNull).findFirst();
        return ans.orElseGet(() -> IGuiContainerHandler.super.getIngredientUnderMouse(screen, mx, my));
    }

    public void unwrap(List<Object> list, Object o) {
        if (o instanceof Either<?, ?>) {
            o = ((Either<?, ?>) o).map(e -> e, e -> e);
        }
        Object left;
        Object right;
        if (o instanceof Map.Entry<?, ?>) {
            left = ((Map.Entry<?, ?>) o).getKey();
            right = ((Map.Entry<?, ?>) o).getValue();
        } else if (o instanceof Pair<?, ?>) {
            left = ((Pair<?, ?>) o).getFirst();
            right = ((Pair<?, ?>) o).getSecond();
        } else return;
        LIST.forEach(e -> list.add(e.predicate(left, right)));
    }

    public interface Wrapper {

        Object predicate(Object left, Object right);

    }

}
