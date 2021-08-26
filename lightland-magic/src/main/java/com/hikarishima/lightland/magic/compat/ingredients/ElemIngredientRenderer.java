package com.hikarishima.lightland.magic.compat.ingredients;

import com.hikarishima.lightland.magic.MagicProxy;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.gui.AbstractHexGui;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ElemIngredientRenderer implements IIngredientRenderer<ElementIngredient> {

    @Override
    public void render(MatrixStack matrixStack, int x, int y, @Nullable ElementIngredient elem) {
        if (elem != null)
            AbstractHexGui.drawElement(matrixStack, x + 8, y + 8, elem.elem, elem.count > 1 ? "" + elem.count : "");
    }

    @Override
    public List<ITextComponent> getTooltip(ElementIngredient elem, ITooltipFlag iTooltipFlag) {
        int has = MagicProxy.getHandler().magicHolder.getElement(elem.elem);
        List<ITextComponent> list = new ArrayList<>();
        list.add(elem.elem.getDesc());
        list.add(Translator.get(has < elem.count, "screen.ability.elemental.desc.count", has));
        return list;
    }
}
