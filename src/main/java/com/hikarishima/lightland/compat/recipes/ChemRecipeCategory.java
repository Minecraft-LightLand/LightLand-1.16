package com.hikarishima.lightland.compat.recipes;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.compat.LightLandJeiPlugin;
import com.hikarishima.lightland.compat.ingredients.ChemIngredient;
import com.hikarishima.lightland.compat.ingredients.Countable;
import com.hikarishima.lightland.config.StringSubstitution;
import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.chem.HashEquationPool;
import com.hikarishima.lightland.registry.ItemRegistry;
import com.lcy0x1.core.chem.AbChemObj;
import com.lcy0x1.core.chem.Equation;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChemRecipeCategory implements IRecipeCategory<Equation> {

    private static final ResourceLocation BG = new ResourceLocation(LightLand.MODID, "textures/jei/background.png");

    private final ResourceLocation id;
    private IDrawable background, icon;

    public ChemRecipeCategory() {
        id = new ResourceLocation(LightLand.MODID, "chem");
    }

    public ChemRecipeCategory init(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(BG, 0, 0, 174, 18);
        icon = guiHelper.createDrawableIngredient(new ItemStack(ItemRegistry.CHEM_BOOK));
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
        HashEquationPool pool = LightLandJeiPlugin.getPool();
        layout.setShapeless();
        AbChemObj in0 = pool.objects.get(t.in[0]);
        AbChemObj in1 = pool.objects.get(t.in[1]);
        AbChemObj r0 = pool.objects.get(t.result[0]);
        AbChemObj r1 = pool.objects.get(t.result[1]);

        set(layout, in0, list, 0, true, 1, 1);
        set(layout, in1, list, 1, true, 50, 1);
        set(layout, r0, list, 2, false, 108, 1);
        set(layout, r1, list, 3, false, 157, 1);
    }

    private static void set(IRecipeLayout layout, @Nullable AbChemObj obj, IIngredients list, int ind, boolean bool, int x, int y) {
        if (obj == null)
            return;
        IGuiIngredientGroup<?> group = layout.getIngredientsGroup(ChemIngredient.getType(obj));
        if (group instanceof IGuiItemStackGroup) {
            x--;
            y--;
        }
        group.init(ind, bool, x, y);
        group.set(list);
    }

}
