package com.hikarishima.lightland.magic.compat.recipes;

import com.google.common.collect.Lists;
import com.hikarishima.lightland.config.StringSubstitution;
import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.chem.HashEquationPool;
import com.hikarishima.lightland.magic.compat.LightLandJeiPlugin;
import com.hikarishima.lightland.magic.compat.ingredients.ChemIngredient;
import com.hikarishima.lightland.magic.compat.ingredients.Countable;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import com.lcy0x1.core.chem.AbChemObj;
import com.lcy0x1.core.chem.Equation;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChemRecipeCategory implements IRecipeCategory<Equation> {

    private static final ResourceLocation BG = new ResourceLocation(LightLandMagic.MODID, "textures/jei/background.png");

    private final ResourceLocation id;
    private IDrawable background, icon;


    public ChemRecipeCategory() {
        id = new ResourceLocation(LightLandMagic.MODID, "chem");
    }

    public ChemRecipeCategory init(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(BG, 0, 0, 174, 18);
        icon = guiHelper.createDrawableIngredient(new ItemStack(MagicItemRegistry.CHEM_BOOK));
        return this;
    }

    @Override
    public ResourceLocation getUid() {
        return id;
    }

    @Override
    public Class<Equation> getRecipeClass() {
        return Equation.class;
    }

    @Override
    public String getTitle() {
        return StringSubstitution.toString(Translator.getContainer("chemistry"));
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(Equation t, IIngredients list) {
        HashEquationPool pool = LightLandJeiPlugin.getPool();
        List<AbChemObj> in_list = Arrays.stream(t.in).map(e -> pool.objects.get(e)).collect(Collectors.toList());
        list.setInputIngredients(in_list.stream().map(ChemIngredient::asItem).filter(Objects::nonNull).map(Ingredient::of).collect(Collectors.toList()));
        list.setInputs(LightLandJeiPlugin.INSTANCE.ELEM_TYPE, in_list.stream().map(ChemIngredient::asElem).filter(Objects::nonNull).collect(Collectors.toList()));
        list.setInputs(LightLandJeiPlugin.INSTANCE.CHEM_TYPE, in_list.stream().map(ChemIngredient::asChem).filter(Objects::nonNull).collect(Collectors.toList()));
        List<AbChemObj> out_list = Arrays.stream(t.result).map(e -> pool.objects.get(e)).collect(Collectors.toList());
        list.setOutputs(VanillaTypes.ITEM, out_list.stream().map(ChemIngredient::asItem).filter(Objects::nonNull).collect(Collectors.toList()));
        list.setOutputs(LightLandJeiPlugin.INSTANCE.ELEM_TYPE, out_list.stream().map(ChemIngredient::asElem).filter(Objects::nonNull).collect(Collectors.toList()));
        list.setOutputs(LightLandJeiPlugin.INSTANCE.CHEM_TYPE, Countable.collect(out_list.stream().map(ChemIngredient::asChem).filter(Objects::nonNull)));
    }

    @Override
    public void setRecipe(IRecipeLayout layout, Equation t, IIngredients list) {
        layout.setShapeless();
        List<IIngredientType<?>> types = Lists.newArrayList(VanillaTypes.ITEM, LightLandJeiPlugin.INSTANCE.ELEM_TYPE, LightLandJeiPlugin.INSTANCE.CHEM_TYPE);

        int c0 = 0;
        for (IIngredientType<?> type : types) {
            c0 = process(layout, type, list, true, c0, 1, 50);
        }
        for (IIngredientType<?> type : types) {
            c0 = process(layout, type, list, false, c0, 108, 157);
        }
    }

    private static <T> int process(IRecipeLayout layout, IIngredientType<T> type, IIngredients list, boolean input, int ind, int x0, int x1) {
        for (List<T> l : input ? list.getInputs(type) : list.getOutputs(type)) {
            IGuiIngredientGroup<T> group = layout.getIngredientsGroup(type);
            int x = x0 + (x1 - x0) * (input ? ind : ind - 2);
            int y = 1;
            if (group instanceof IGuiItemStackGroup) {
                x--;
                y--;
            }
            group.init(ind, input, x, y);
            group.set(ind, l);
            ind++;
        }
        return ind;
    }

}
