package com.hikarishima.lightland.magic.compat.recipes;

import com.hikarishima.lightland.config.StringSubstitution;
import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.recipe.IMagicCraftRecipe;
import com.hikarishima.lightland.magic.recipe.ShapelessCraftRecipe;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicCraftRecipeCategory implements IRecipeCategory<IMagicCraftRecipe<?>> {

    private static final ResourceLocation BG = new ResourceLocation(LightLandMagic.MODID, "textures/jei/background.png");

    private final ResourceLocation id;
    private IDrawable background, icon;

    public MagicCraftRecipeCategory() {
        this.id = new ResourceLocation(LightLandMagic.MODID, "magic_craft");
    }

    public MagicCraftRecipeCategory init(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(BG, 0, 36, 145, 54);
        icon = guiHelper.createDrawableIngredient(new ItemStack(MagicItemRegistry.I_RITUAL_CORE));
        return this;
    }

    @Override
    public ResourceLocation getUid() {
        return id;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Class getRecipeClass() {
        return IMagicCraftRecipe.class;
    }

    @Override
    public String getTitle() {
        return StringSubstitution.toString(Translator.getContainer("magic_craft"));
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
    public void setIngredients(IMagicCraftRecipe<?> r, IIngredients list) {
        if (r instanceof ShapelessCraftRecipe) {
            ShapelessCraftRecipe sl = (ShapelessCraftRecipe) r;
            List<Ingredient> input = new ArrayList<>();
            for (IMagicCraftRecipe.Entry ent : sl.side) {
                input.add(ent.input);
            }
            while (input.size() < 4) {
                input.add(Ingredient.EMPTY);
            }
            input.add(4, sl.core.input);
            list.setInputIngredients(input);
            List<ItemStack> output = new ArrayList<>();
            for (IMagicCraftRecipe.Entry ent : sl.side) {
                output.add(ent.output);
            }
            while (output.size() < 4) {
                output.add(ItemStack.EMPTY);
            }
            output.add(4, sl.core.output);
            list.setOutputs(VanillaTypes.ITEM, output);
        }
        //TODO shaped
    }

    @Override
    public void setRecipe(IRecipeLayout layout, IMagicCraftRecipe<?> r, IIngredients list) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                set(layout.getItemStacks(), list.getInputs(VanillaTypes.ITEM).get(i * 3 + j), i * 3 + j, true, j * 18, i * 18);
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                set(layout.getItemStacks(), list.getOutputs(VanillaTypes.ITEM).get(i * 3 + j), i * 3 + j, false, 90 + j * 18, i * 18);
            }
        }
    }

    private static <T> void set(IGuiIngredientGroup<T> group, List<T> t, int ind, boolean bool, int x, int y) {
        group.init(ind, bool, x, y);
        group.set(ind, t);
    }

}
