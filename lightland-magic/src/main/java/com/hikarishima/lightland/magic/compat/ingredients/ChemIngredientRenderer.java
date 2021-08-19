package com.hikarishima.lightland.magic.compat.ingredients;

import com.google.common.collect.Lists;
import com.hikarishima.lightland.magic.MagicProxy;
import com.hikarishima.lightland.magic.chem.ChemObj;
import com.hikarishima.lightland.magic.gui.container.ChemScreen;
import com.hikarishima.lightland.proxy.Proxy;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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
    public void render(MatrixStack matrix, int x, int y, @Nullable ChemIngredient chem) {
        if (chem != null) {
            ChemScreen.render(matrix, ChemObj.cast(MagicProxy.getHandler(), chem.obj), x, y);
            FontRenderer font = Minecraft.getInstance().font;
            String s = chem.count > 1 ? "" + chem.count : "";
            matrix.pushPose();
            matrix.translate(0, 0, 200);
            font.draw(matrix, s, (float) (x + 8 + 11 - 1 - font.width(s)), (float) (y + 8 + 2), 0x404040);
            font.draw(matrix, s, (float) (x + 8 + 11 - 2 - font.width(s)), (float) (y + 8 + 1), 0xFFFFFF);
            matrix.popPose();
        }
    }

    @Override
    public List<ITextComponent> getTooltip(ChemIngredient chem, ITooltipFlag iTooltipFlag) {
        return Lists.newArrayList(chem.obj instanceof ChemObj ? ((ChemObj<?, ?>) chem.obj).getDesc() : new StringTextComponent("???"));
    }
}
