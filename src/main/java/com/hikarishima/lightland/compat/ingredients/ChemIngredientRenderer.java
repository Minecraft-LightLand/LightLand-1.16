package com.hikarishima.lightland.compat.ingredients;

import com.google.common.collect.Lists;
import com.hikarishima.lightland.magic.chem.ChemObj;
import com.hikarishima.lightland.magic.gui.container.ChemScreen;
import com.hikarishima.lightland.proxy.Proxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChemIngredientRenderer implements IIngredientRenderer<ChemIngredient> {

    @Override
    public void render(MatrixStack matrixStack, int x, int y, @Nullable ChemIngredient chem) {
        if (chem != null)
            ChemScreen.render(matrixStack, ChemObj.cast(Proxy.getHandler(), chem.obj), x, y);
    }

    @Override
    public List<ITextComponent> getTooltip(ChemIngredient chem, ITooltipFlag iTooltipFlag) {
        return Lists.newArrayList(chem.obj instanceof ChemObj ? ((ChemObj<?, ?>) chem.obj).getDesc() : new StringTextComponent("???"));
    }
}
